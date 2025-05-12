package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.evandrorenan.infra.adapters.openfeature.TextFileFeatureFlagAdapter.ResourceFile;
import br.com.featureflagsdkjava.domain.model.Flag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TextFileFeatureFlagAdapterTest {

    TextFileFeatureFlagAdapter adapter =
            new TextFileFeatureFlagAdapter(new ResourceFile("feature-flags-empty.txt"));

    @Test
    void findAll_noFlags_returnsEmptyList() {
        TextFileFeatureFlagAdapter adapter =
                new TextFileFeatureFlagAdapter(new ResourceFile("feature-flags-empty.txt"));
        List<Flag> result = adapter.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_multipleFlags_returnsMappedList() {
        TextFileFeatureFlagAdapter adapter =
                new TextFileFeatureFlagAdapter(new ResourceFile("feature-flags.txt"));
        List<Flag> result = adapter.findAll();

        assertEquals(4, result.size());
    }

    @Test
    void findFlagsByType_existingType_returnsMappedList() {
        TextFileFeatureFlagAdapter adapter =
                new TextFileFeatureFlagAdapter(new ResourceFile("feature-flags.txt"));

        List<Flag> result = adapter.findFlagsByType(Flag.Type.STRING);

        assertEquals(3, result.size());
    }

    @Test
    void findFlagsByType_nonExistingType_returnsEmptyList() {
        TextFileFeatureFlagAdapter adapter =
                new TextFileFeatureFlagAdapter(new ResourceFile("feature-flags.txt"));

        List<Flag> result = adapter.findFlagsByType(Flag.Type.NUMBER);

        assertEquals(0, result.size());
    }

    @Test
    void findByFlagName_existingName_returnsMappedOptionalFlag() {
        TextFileFeatureFlagAdapter adapter =
                new TextFileFeatureFlagAdapter(new ResourceFile("feature-flags.txt"));

        Optional<Flag> result = adapter.findByFlagName("flag1");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("flag1", result.get().getName());
        assertEquals(Flag.Type.STRING, result.get().getType());
        assertEquals(Flag.State.ENABLED, result.get().getState());
        assertEquals("OFF", result.get().getDefaultVariant());
        assertEquals(Map.of("OFF", "", "ON", "release1"), result.get().getVariants());
        assertEquals(
                "{\"if\": [{\"in\": [{\"var\": \"request.body.cdCpfCnpj\"}, [\"11122233355\", \"1234567801\"]]}, \"ON\"]}",
                result.get().getTargeting());
    }

    @Test
    void findByFlagName_nonExistingName_returnsEmptyOptional() {
        TextFileFeatureFlagAdapter adapter =
                new TextFileFeatureFlagAdapter(new ResourceFile("feature-flags.txt"));

        Optional<Flag> result = adapter.findByFlagName("nonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void save_validFlag_savesAndReturnsMappedFlag() {
        assertThrows(UnsupportedOperationException.class, () -> adapter.save(null));
    }
}