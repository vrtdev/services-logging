package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AbstractBreadcrumbAuditAspectTest {

    public static final String RETURN_VALUE = "aString";

    @Mock
    ProceedingJoinPoint joinPoint;



    @Test
    public void logAround_whenCalled_thenHandleJoinPoint() throws Throwable {
        AbstractBreadcrumbAuditAspect abstractBreadcrumbAuditAspect = new AbstractBreadcrumbAuditAspect() {
            @Override
            protected Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
                return RETURN_VALUE;
            }
        };


        Object obj = abstractBreadcrumbAuditAspect.logAround(joinPoint);
        assertTrue(obj instanceof String && obj.equals(RETURN_VALUE));
    }
}