package parapharmacie.para.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ResourceReloadService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceReloadService.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final String dataDirectory = "data/";
    private final String imageDirectory = "images/products/";
    private WatchService watchService;
    private final Map<WatchKey, Path> watchKeys = new HashMap<>();
    private final Map<String, Long> fileLastModified = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread watcherThread;

    @Value("${spring.web.resources.static-locations:classpath:/static/}")
    private String staticResourceLocation;

    public ResourceReloadService(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        logger.info("Initializing ResourceReloadService");

        // Déterminer le chemin absolu du répertoire de ressources
        String projectDir = new File(".").getCanonicalPath();
        String staticPath = Paths.get(projectDir, "backend","src", "main", "resources", "static").toString();

        logger.info("Project directory: {}", projectDir);
        logger.info("Static path: {}", staticPath);

        // Créer le WatchService
        this.watchService = FileSystems.getDefault().newWatchService();

        // Créer et surveiller les répertoires
        Path dataPath = Paths.get(staticPath, dataDirectory);
        Path imagePath = Paths.get(staticPath, imageDirectory);

        createAndWatchDirectory(dataPath);
        createAndWatchDirectory(imagePath);

        // Initialiser le cache des dernières modifications
        initializeLastModifiedCache(dataPath);
        initializeLastModifiedCache(imagePath);

        // Démarrer le thread de surveillance
        startWatcherThread();

        logger.info("ResourceReloadService initialized successfully");
    }

    private void createAndWatchDirectory(Path path) throws IOException {
        Files.createDirectories(path);
        WatchKey key = path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        watchKeys.put(key, path);
        logger.info("Now watching directory: {}", path);
    }

    private void startWatcherThread() {
        watcherThread = new Thread(() -> {
            while (running.get()) {
                try {
                    WatchKey key = watchService.take();
                    Path dir = watchKeys.get(key);

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path filePath = dir.resolve((Path) event.context());

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        logger.info("File event detected: {} - {}", kind, filePath);

                        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            handleFileModification(filePath);
                        }
                    }

                    if (!key.reset()) {
                        watchKeys.remove(key);
                        if (watchKeys.isEmpty()) {
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    if (running.get()) {
                        logger.error("Watcher thread interrupted", e);
                    }
                }
            }
        });
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private void handleFileModification(Path filePath) {
        try {
            String absolutePath = filePath.toAbsolutePath().toString();
            long lastModified = Files.getLastModifiedTime(filePath).toMillis();
            Long previousModified = fileLastModified.get(absolutePath);

            if (previousModified == null || lastModified > previousModified) {
                fileLastModified.put(absolutePath, lastModified);
                logger.info("File updated: {}", filePath);
            }
        } catch (IOException e) {
            logger.error("Error handling file modification for {}", filePath, e);
        }
    }

    private void initializeLastModifiedCache(Path directory) {
        try {
            Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            fileLastModified.put(
                                    path.toAbsolutePath().toString(),
                                    Files.getLastModifiedTime(path).toMillis()
                            );
                        } catch (IOException e) {
                            logger.error("Error initializing cache for {}", path, e);
                        }
                    });
        } catch (IOException e) {
            logger.error("Error walking directory {}", directory, e);
        }
    }

    public <T> T loadJsonData(String filename, Class<T> valueType) throws IOException {
        Path dataPath = Paths.get(new File(".").getCanonicalPath(), "src", "main", "resources", "static", dataDirectory, filename);
        logger.info("Loading JSON data from: {}", dataPath);
        return objectMapper.readValue(dataPath.toFile(), valueType);
    }

    public void saveJsonData(String filename, Object data) throws IOException {
        Path dataPath = Paths.get(new File(".").getCanonicalPath(), "src", "main", "resources", "static", dataDirectory, filename);
        logger.info("Saving JSON data to: {}", dataPath);
        objectMapper.writeValue(dataPath.toFile(), data);
    }

    public void saveImage(String filename, MultipartFile image) throws IOException {
        Path imagePath = Paths.get(new File(".").getCanonicalPath(), "src", "main", "resources", "static", imageDirectory, filename);
        logger.info("Saving image to: {}", imagePath);
        image.transferTo(imagePath.toFile());
    }

    @PreDestroy
    public void shutdown() {
        running.set(false);
        watcherThread.interrupt();
        try {
            watchService.close();
        } catch (IOException e) {
            logger.error("Error closing watch service", e);
        }
    }
}