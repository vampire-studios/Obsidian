package io.github.vampirestudios.obsidian.client.resource;

import com.google.common.base.Charsets;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.AbstractFileResourcePack;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.resource.pack.metadata.PackResourceMetadata;
import net.minecraft.resource.pack.metadata.ResourceMetadataReader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class SPResourcePack extends AbstractFileResourcePack implements ResourcePack {

    private static final String NAME = "spoornpacks";
    private static final Pattern RESOURCE_PACK_PATH = Pattern.compile("[a-z0-9-_]+");
    private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(new TranslatableText("obsidian.metadata.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion()));

    private final ResourceType resourceType;
    private final String id;
    private Set<String> namespaces;
    private final String separator;
    private final Path basePath;

    public SPResourcePack(String id, ResourceType resourceType, Path basePath) {
        super(null);
        this.id = id;
        this.resourceType = resourceType;
        this.basePath = basePath.resolve(id).resolve("resources").toAbsolutePath().normalize();
        this.separator = basePath.getFileSystem().getSeparator();
    }

    private Path getPath(String filename) {
        Path childPath = basePath.resolve(filename.replace("/", separator)).toAbsolutePath().normalize();

        if (childPath.startsWith(basePath) && Files.exists(childPath)) {
            return childPath;
        } else {
            return null;
        }
    }

    @Override
    protected boolean containsFile(String filename) {
        // Pack Metadata is always available
        if (PACK_METADATA_NAME.equals(filename)) {
            return true;
        }

        Path path = getPath(filename);
        return path != null && Files.isRegularFile(path);
    }

    @Override
    protected InputStream openFile(String fileName) throws IOException {
        InputStream stream;

        Path path = getPath(fileName);

        if (path != null && Files.isRegularFile(path)) {
            return Files.newInputStream(path);
        }

        stream = openDefault(this.resourceType, fileName);

        if (stream != null) {
            return stream;
        }

        // ReloadableResourceManagerImpl gets away with FileNotFoundException.
        throw new FileNotFoundException("\"" + fileName + "\" in Obsidian addon \"" + this.id + "\"");
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        //System.out.println("### findresources " + namespace + "/" + prefix);
        List<Identifier> ids = new ArrayList<>();
        String path = prefix.replace("/", separator);

        Path namespacePath = getPath(type.getDirectory() + "/" + namespace);

        //System.out.println("### namespacepath: " + namespacePath);
        if (namespacePath != null) {
            Path searchPath = namespacePath.resolve(path).toAbsolutePath().normalize();
            //System.out.println("### searchpath: " + searchPath);

            if (Files.exists(searchPath)) {
                try {
                    Files.walk(searchPath, maxDepth)
                            .filter(Files::isRegularFile)
                            .filter((p) -> {
                                String filename = p.getFileName().toString();
                                //System.out.println("### filename: " + filename);
                                return !filename.endsWith(".mcmeta") && pathFilter.test(filename);
                            })
                            .map(namespacePath::relativize)
                            .map((p) -> p.toString().replace(separator, "/"))
                            .forEach((s) -> {
                                try {
                                    //System.out.println("### path: " + s);
                                    ids.add(new Identifier(namespace, s));
                                } catch (InvalidIdentifierException e) {
                                    Obsidian.LOGGER.error(e.getMessage(), e);
                                }
                            });
                } catch (IOException e) {
                    Obsidian.LOGGER.warn("findResources at " + path + " in namespace " + namespace + " failed!", e);
                }
            }
        }

        return ids;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if (this.namespaces == null) {
            Path file = getPath(type.getDirectory());

            assert file != null;
            if (!Files.isDirectory(file)) {
                return Collections.emptySet();
            }

            Set<String> namespaces = new HashSet<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(file, Files::isDirectory)) {
                for (Path path : stream) {
                    String s = path.getFileName().toString();
                    // s may contain trailing slashes, remove them
                    s = s.replace(separator, "");

                    if (RESOURCE_PACK_PATH.matcher(s).matches()) {
                        namespaces.add(s);
                    } else {
                        Obsidian.LOGGER.error("Invalid namespace format at {}", path);
                    }
                }
            } catch (IOException e) {
                Obsidian.LOGGER.error("Could not get namespaces", e);
            }

            // Allow minecraft tags from mods as well
            // TODO: Optimize.  This will cause all SPResourcePacks to be invoked for "minecraft" tags
            namespaces.add("minecraft");
            this.namespaces = namespaces;
        }

        return this.namespaces;
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        if (metaReader.getKey().equals("pack")) {
            return (T) DEFAULT_PACK_METADATA;
        } else {
            return null;
        }
    }

    public static InputStream openDefault(ResourceType type, String filename) {
        if ("pack.mcmeta".equals(filename)) {
//            description = description.replaceAll("\"", "\\\"");
            String pack = "{\"pack\":{\"pack_format\":" + type.getPackVersion(SharedConstants.getGameVersion()) + ",\"description\":\"This is a test\"}}";
            return IOUtils.toInputStream(pack, Charsets.UTF_8);
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void close() {
    }
}