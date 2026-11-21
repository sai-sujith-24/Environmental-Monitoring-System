# Concurrent Environmental Monitoring System (Java)

Demonstrates Java multithreading using three independent simulated monitors:
- Temperature: generates a value every 1 second
- Humidity: generates a value every 2 seconds
- Air quality: generates an AQI value every 3 seconds

Each thread tracks normal, warning, and critical readings, then the main thread uses `join()` to await completion and print an aggregate report. Readings are generated with `Random` and **do not come from physical sensors**.

## Run
```bash
javac EnvironmentalMonitoringSystemV2.java
java EnvironmentalMonitoringSystemV2
```

## Concepts
Thread lifecycle, independent state, interruption handling, thread joining, aggregation of results. Thresholds are educational examples, not medical or official environmental standards. Consider extracting them into named constants and adding tests for boundary values.
