package org.jboss.sbomer.enhancer.core.port.spi;

import java.util.List;

public interface SbomStorage {
    List<String> download(List<String> urls);
    List<String> upload(List<String> sboms);
}
