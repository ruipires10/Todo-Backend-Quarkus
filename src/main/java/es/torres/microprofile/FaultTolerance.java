package es.torres.microprofile;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;

@ApplicationScoped
public class FaultTolerance {

    @Retry(maxRetries = 4)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.6, delay = 1000)
    public String fetchData() {
        throw new RuntimeException("Service unavailable!");
    }
}
