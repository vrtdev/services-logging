package be.vrt.services.logging.api.audit.annotation;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LevelTest {

    private static final Level LEVEL_DEFAULT = Level.INFO;

    @Test
    public void allLevelsMapToLogback() throws Exception {
        for (Level level : Level.values()) {
            assertThat(ch.qos.logback.classic.Level.toLevel(level.name()).levelStr, is(level.name()));
        }
    }

    @Test
    public void fromMapsAllLevels() throws Exception {
        for (Level level : Level.values()) {
            assertThat(Level.from(level.name()), is(level));
        }
    }

    @Test
    public void fromReturnsDefaultForInvalid() throws Exception {
        assertThat(Level.from(null), is(LEVEL_DEFAULT));
        assertThat(Level.from("invalid"), is(LEVEL_DEFAULT));
        for (Level level : Level.values()) {
            assertThat(Level.from(level.name().toLowerCase()), is(LEVEL_DEFAULT));
        }
    }
}