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

    private AnnotatedMethodsClass annotatedMethodsClass;
    private AnnotatedClass annotatedClass;

    @Before
    public void setUp() throws Exception {
        AnnotatedMethodsClass target = new AnnotatedMethodsClassImpl();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        LogWithLevelAspect logWithLevelAspect = new LogWithLevelAspect();
        factory.addAspect(logWithLevelAspect);
        annotatedMethodsClass = factory.getProxy();

        AspectJProxyFactory annotatedClassProxyFactory = new AspectJProxyFactory(new AnnotatedClassImpl());
        annotatedClassProxyFactory.addAspect(logWithLevelAspect);
        annotatedClass = annotatedClassProxyFactory.getProxy();
    }

    @Test
    public void levelIsSetOnMethods() throws Exception {
        assertThat(annotatedMethodsClass.info(), Matchers.is(Level.INFO.name()));
        assertThat(annotatedMethodsClass.all(), Matchers.is(Level.ALL.name()));
        assertThat(annotatedMethodsClass.debug(), Matchers.is(Level.DEBUG.name()));
        assertThat(annotatedMethodsClass.error(), Matchers.is(Level.ERROR.name()));
        assertThat(annotatedMethodsClass.off(), Matchers.is(Level.OFF.name()));
        assertThat(annotatedMethodsClass.trace(), Matchers.is(Level.TRACE.name()));
        assertThat(annotatedMethodsClass.warn(), Matchers.is(Level.WARN.name()));
        assertThat(annotatedMethodsClass.none(), Matchers.is(Level.INFO.name()));
    }

    @Test
    public void levelIsSetOnClass() throws Exception {
        assertThat(annotatedClass.level(), Matchers.is(Level.OFF.name()));
    }

    public interface AnnotatedMethodsClass {
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

    public interface AnnotatedClass {
        String level();
    }

    @LogWithLevel(Level.OFF)
    public static class AnnotatedClassImpl implements AnnotatedClass {
        @Override
        public String level() {
            return LogTransaction.getLevel();
        }
    }

    public static class AnnotatedMethodsClassImpl implements AnnotatedMethodsClass {
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
}