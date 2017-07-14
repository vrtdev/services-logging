package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.api.audit.annotation.Level;
import be.vrt.services.logging.api.audit.annotation.AuditLogLevel;
import be.vrt.services.logging.log.common.LogTransaction;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.Assert.assertThat;

public class AuditLogLevelAspectTest {

    private AnnotatedMethodsClass annotatedMethodsClass;
    private AnnotatedClass annotatedClass;

    @Before
    public void setUp() throws Exception {
        AnnotatedMethodsClass target = new AnnotatedMethodsClassImpl();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        AuditLogLevelAspect auditLogLevelAspect = new AuditLogLevelAspect();
        factory.addAspect(auditLogLevelAspect);
        annotatedMethodsClass = factory.getProxy();

        AspectJProxyFactory annotatedClassProxyFactory = new AspectJProxyFactory(new AnnotatedClassImpl());
        annotatedClassProxyFactory.addAspect(auditLogLevelAspect);
        annotatedClass = annotatedClassProxyFactory.getProxy();
    }

    @Test
    public void levelIsSetOnMethods() throws Exception {
        assertThat(annotatedMethodsClass.info(), Matchers.is(Level.INFO.name()));
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
        @AuditLogLevel(Level.INFO)
        String info();

        @AuditLogLevel(Level.DEBUG)
        String debug();

        @AuditLogLevel(Level.ERROR)
        String error();

        @AuditLogLevel(Level.OFF)
        String off();

        @AuditLogLevel(Level.TRACE)
        String trace();

        @AuditLogLevel(Level.WARN)
        String warn();

        String none();
    }

    public interface AnnotatedClass {
        String level();
    }

    @AuditLogLevel(Level.OFF)
    public static class AnnotatedClassImpl implements AnnotatedClass {
        @Override
        public String level() {
            return LogTransaction.getLevel();
        }
    }

    public static class AnnotatedMethodsClassImpl implements AnnotatedMethodsClass {
        @Override
        @AuditLogLevel(Level.INFO)
        public String info() {
            return LogTransaction.getLevel();
        }

        @Override
        @AuditLogLevel(Level.DEBUG)
        public String debug() {
            return LogTransaction.getLevel();
        }

        @Override
        @AuditLogLevel(Level.ERROR)
        public String error() {
            return LogTransaction.getLevel();
        }

        @Override
        @AuditLogLevel(Level.OFF)
        public String off() {
            return LogTransaction.getLevel();
        }

        @Override
        @AuditLogLevel(Level.TRACE)
        public String trace() {
            return LogTransaction.getLevel();
        }

        @Override
        @AuditLogLevel(Level.WARN)
        public String warn() {
            return LogTransaction.getLevel();
        }

        @Override
        public String none() {
            return LogTransaction.getLevel();
        }
    }
}