package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.api.audit.annotation.Level;
import be.vrt.services.logging.api.audit.annotation.LogWithLevel;
import be.vrt.services.logging.log.common.LogTransaction;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.Assert.assertThat;

public class LogWithLevelAspectTest {

    private AnnotatedClass annotatedClass;

    @Before
    public void setUp() throws Exception {
        AnnotatedClass target = new AnnotatedClassImpl();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        LogWithLevelAspect logWithLevelAspect = new LogWithLevelAspect();
        factory.addAspect(logWithLevelAspect);
        annotatedClass = factory.getProxy();
    }

    @Test
    public void levelIsSet() throws Exception {
        assertThat(annotatedClass.info(), Matchers.is(Level.INFO.name()));
        assertThat(annotatedClass.all(), Matchers.is(Level.ALL.name()));
        assertThat(annotatedClass.debug(), Matchers.is(Level.DEBUG.name()));
        assertThat(annotatedClass.error(), Matchers.is(Level.ERROR.name()));
        assertThat(annotatedClass.off(), Matchers.is(Level.OFF.name()));
        assertThat(annotatedClass.trace(), Matchers.is(Level.TRACE.name()));
        assertThat(annotatedClass.warn(), Matchers.is(Level.WARN.name()));
        assertThat(annotatedClass.none(), Matchers.is(Level.INFO.name()));
    }

    public static class AnnotatedClassImpl implements AnnotatedClass {
        @Override
        @LogWithLevel(Level.INFO)
        public String info() {
            return LogTransaction.getLevel();
        }

        @Override
        @LogWithLevel(Level.ALL)
        public String all() {
            return LogTransaction.getLevel();
        }

        @Override
        @LogWithLevel(Level.DEBUG)
        public String debug() {
            return LogTransaction.getLevel();
        }

        @Override
        @LogWithLevel(Level.ERROR)
        public String error() {
            return LogTransaction.getLevel();
        }

        @Override
        @LogWithLevel(Level.OFF)
        public String off() {
            return LogTransaction.getLevel();
        }

        @Override
        @LogWithLevel(Level.TRACE)
        public String trace() {
            return LogTransaction.getLevel();
        }

        @Override
        @LogWithLevel(Level.WARN)
        public String warn() {
            return LogTransaction.getLevel();
        }

        @Override
        public String none() {
            return LogTransaction.getLevel();
        }
    }

    public static interface AnnotatedClass {
        @LogWithLevel(Level.INFO)
        String info();

        @LogWithLevel(Level.ALL)
        String all();

        @LogWithLevel(Level.DEBUG)
        String debug();

        @LogWithLevel(Level.ERROR)
        String error();

        @LogWithLevel(Level.OFF)
        String off();

        @LogWithLevel(Level.TRACE)
        String trace();

        @LogWithLevel(Level.WARN)
        String warn();

        String none();
    }
}