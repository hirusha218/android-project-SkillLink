package lk.javainstitute.skilllink.model;

public class Post {
    private final String title;
    private final String imageUrl;

    public Post(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
