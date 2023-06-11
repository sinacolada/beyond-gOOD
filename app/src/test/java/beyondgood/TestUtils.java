package beyondgood;

import java.nio.file.Path;

public class TestUtils {
    
    /** 
     * Gets resource filepath string given resource name.
     * 
     * @param resourceName the exact filename of the resource.
     * @return String filepath of specified resource.
     */
    public static String getResourcePath(String resourceName) {
        Path path = Path.of("src", "test", "resources", "file_saves", resourceName);
        return path.toFile().getAbsolutePath();
    }
}
