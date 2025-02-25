package study.blogapi.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import study.blogapi.exception.ResponseEntityErrorException;
import study.blogapi.model.Album;
import study.blogapi.payload.AlbumResponse;
import study.blogapi.payload.ApiResponse;
import study.blogapi.payload.PagedResponse;
import study.blogapi.payload.PhotoResponse;
import study.blogapi.payload.request.AlbumRequest;
import study.blogapi.security.CurrentUser;
import study.blogapi.security.UserPrincipal;
import study.blogapi.service.AlbumService;
import study.blogapi.service.PhotoService;
import study.blogapi.utils.AppConstants;
import study.blogapi.utils.AppUtils;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;
    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }
    @GetMapping
    public PagedResponse<AlbumResponse> getAllAlbums(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return albumService.getAllAlbums(page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Album> addAlbum(@Valid @RequestBody AlbumRequest albumRequest, @CurrentUser UserPrincipal currentUser) {
        return albumService.addAlbum(albumRequest, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable(name = "id") Long id) {
        return albumService.getAlbum(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AlbumResponse> updateAlbum(@PathVariable(name = "id") Long id, @Valid @RequestBody AlbumRequest newAlbum,
                                                     @CurrentUser UserPrincipal currentUser) {
        return albumService.updateAlbum(id, newAlbum, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteAlbum(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        return albumService.deleteAlbum(id, currentUser);
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<PagedResponse<PhotoResponse>> getAllPhotosByAlbum(@PathVariable(name = "id") Long id,
                                                                            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

        PagedResponse<PhotoResponse> response = photoService.getAllPhotosByAlbum(id, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
