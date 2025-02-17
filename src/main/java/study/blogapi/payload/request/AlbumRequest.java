package study.blogapi.payload.request;


import lombok.Data;
import study.blogapi.model.Photo;
import study.blogapi.model.user.User;
import study.blogapi.payload.UserDateAuditPayload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class AlbumRequest extends UserDateAuditPayload {

    private Long id;

    private String title;

    private User user;

    private List<Photo> photo;

    public List<Photo> getPhoto() {

        return photo == null ? null : new ArrayList<>(photo);
    }

    public void setPhoto(List<Photo> photo) {

        if (photo == null) {
            this.photo = null;
        } else {
            this.photo = Collections.unmodifiableList(photo);
        }
    }
}