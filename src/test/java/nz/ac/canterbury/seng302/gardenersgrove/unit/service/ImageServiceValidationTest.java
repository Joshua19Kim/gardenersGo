package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import static org.mockito.Mockito.*;

public class ImageServiceValidationTest {

    private static ImageService imageService;

    @BeforeAll()
    public static void setUp() {
        imageService = new ImageService(mock(GardenerFormService.class), mock(PlantService.class), mock(PlantIdentificationService.class));
    }

    @Test
    public void FileSizeChecked_LessThan10MB_TrueReturned() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "small_file.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World!".getBytes());
        Assertions.assertTrue(imageService.isFileSizeValid(mockMultipartFile));

    }

    @Test
    public void FileSizeChecked_GreaterThan10MB_FalseReturned() {
        byte[] largeBytes = new byte[11 *1024 * 1024];
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "large_file.txt",
                MediaType.TEXT_PLAIN_VALUE, largeBytes);
        Assertions.assertFalse(imageService.isFileSizeValid(mockMultipartFile));

    }

    @Test
    public void FileTypeChecked_InvalidExtension_FalseReturned() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "text_file.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World!".getBytes());
        Assertions.assertFalse(imageService.checkValidExtension(mockMultipartFile));
    }

    @Test
    public void FileTypeChecked_JPGExtension_TrueReturned() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "jpg_file.jpg",
                MediaType.IMAGE_JPEG_VALUE, "Hello World!".getBytes());
        Assertions.assertTrue(imageService.checkValidExtension(mockMultipartFile));
    }

    @Test
    public void FileTypeChecked_PNGExtension_TrueReturned() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "png_file.png",
                MediaType.IMAGE_PNG_VALUE, "Hello World!".getBytes());
        Assertions.assertTrue(imageService.checkValidExtension(mockMultipartFile));
    }

    @Test
    public void FileTypeChecked_SVGExtension_TrueReturned() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "svg_file.svg",
                "image/svg+xml", "Hello World!".getBytes());
        Assertions.assertTrue(imageService.checkValidExtension(mockMultipartFile));
    }

}
