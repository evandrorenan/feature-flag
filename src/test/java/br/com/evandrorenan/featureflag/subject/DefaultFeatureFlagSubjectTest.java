package br.com.evandrorenan.featureflag.subject;

import br.com.evandrorenan.featureflag.observer.FeatureFlagObserver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DefaultFeatureFlagSubjectTest {

    @Test
    void testEnable() {
        DefaultFeatureFlagSubject subject = new DefaultFeatureFlagSubject();
        TestObserver observer = new TestObserver();
        subject.addObserver(observer);

        assertFalse(subject.isEnabled()); // Initial state is disabled

        subject.enable();

        assertTrue(subject.isEnabled()); // State should be enabled after calling enable()
        assertTrue(observer.isUpdated()); // Observer should be notified
    }

    @Test
    void testDisable() {
        DefaultFeatureFlagSubject subject = new DefaultFeatureFlagSubject();
        TestObserver observer = new TestObserver();
        subject.addObserver(observer);

        subject.enable(); // Enable initially

        assertTrue(subject.isEnabled()); // Initial state is enabled

        subject.disable();

        assertFalse(subject.isEnabled()); // State should be disabled after calling disable()
        assertTrue(observer.isUpdated()); // Observer should be notified
    }

    @Test
    void testAddRemoveObserver() {
        DefaultFeatureFlagSubject subject = new DefaultFeatureFlagSubject();
        TestObserver observer = new TestObserver();

        assertFalse(subject.isEnabled()); // Initial state is disabled

        subject.addObserver(observer);

        subject.enable();

        assertTrue(observer.isUpdated()); // Observer should be notified after enable()

        // Remove observer
        subject.removeObserver(observer);

        subject.disable();

        assertTrue(observer.isUpdated()); // Observer should not be notified after removeObserver()
    }

    private static class TestObserver implements FeatureFlagObserver {
        private boolean updated = false;

        public boolean isUpdated() {
            return updated;
        }

        @Override
        public void update(FeatureFlagSubject subject) {
            updated = true;
        }
    }
}
