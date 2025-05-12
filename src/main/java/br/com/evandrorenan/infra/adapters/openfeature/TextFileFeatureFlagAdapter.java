package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.evandrorenan.domain.ports.in.FeatureFlagPersistencePort;
import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Slf4j
@Service("databaseFeatureFlagQuery")
@Primary
@Profile("disabled-database")
public class TextFileFeatureFlagAdapter implements FeatureFlagQueryPort, FeatureFlagPersistencePort {
    private final ResourceFile resourceFile;
    private final FlagFileReader flagFileReader;

    public TextFileFeatureFlagAdapter(ResourceFile resourceFile) {
        this.resourceFile = resourceFile;
        this.flagFileReader = new FlagFileReader(resourceFile.name);
    }

    @Override
    public List<Flag> findAll() {
        return flagFileReader.readFlags().all();
    }

    @Override
    public List<Flag> findFlagsByType(Flag.Type type) {
        return flagFileReader.readFlags().byType(type);
    }

    @Override
    public Optional<Flag> findByFlagName(String flagName) {
        return flagFileReader.readFlags().byName(flagName);
    }

    @Override
    public Flag save(Flag flag) {
        throw new UnsupportedOperationException("Updates not allowed on disabled-database profile");
    }

    static class FlagFileReader {

        private final String fileName;
        private final ObjectMapper objectMapper;

        FlagFileReader(String fileName) {
            this.fileName = fileName;
            this.objectMapper = new ObjectMapper();
        }

        FlagCollection readFlags() {
            InputStream inputStream = getResourceFile();
            if (inputStream == null) return new FlagCollection(Collections.emptyList());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                List<Flag> flags = readFlagsFromFile(reader);
                return new FlagCollection(flags);
            } catch (IOException e) {
                log.error("Failed to read feature flags from file: {}", fileName, e);
                return new FlagCollection(Collections.emptyList());
            }
        }

        private List<Flag> readFlagsFromFile(BufferedReader reader) {
            return reader.lines()
                         .filter(line -> !emptyLinesAndComments(line))
                         .map(this::deserializeToFlag)
                         .filter(Objects::nonNull)
                         .toList();
        }

        private boolean emptyLinesAndComments(String line) {
            return line.isEmpty() || line.startsWith("#");
        }

        private InputStream getResourceFile() {
            InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
            if (input == null) {
                log.warn("No flags found on resource file {}", fileName);
            }
            return input;
        }

        private Flag deserializeToFlag(String json) {
            try {
                return objectMapper.readValue(json, Flag.class);
            } catch (JsonProcessingException e) {
                log.error("Invalid flag JSON: {}", json, e);
                return null;
            }
        }
    }

    static class FlagCollection {

        private final List<Flag> flags;

        FlagCollection(List<Flag> flags) {
            this.flags = flags;
        }

        List<Flag> all() {
            return new ArrayList<>(flags);
        }

        List<Flag> byType(Flag.Type type) {
            return flags.stream()
                        .filter(f -> f.getType() == type)
                        .toList();
        }

        Optional<Flag> byName(String name) {
            return flags.stream()
                        .filter(f -> f.getName().equalsIgnoreCase(name))
                        .findFirst();
        }
    }

    public record ResourceFile(String name) {}
}


