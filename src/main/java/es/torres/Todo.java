package es.torres;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.ws.rs.core.UriBuilder;

@Entity
public class Todo extends PanacheEntity {

    private String title;
    private Boolean completed;
    @Column(name = "\"order\"")
    private Integer order;
    private URL url;

    public URL getUrl() throws URISyntaxException, MalformedURLException {
        if (this.id != null) {
            return UriBuilder.fromUri(url.toURI()).scheme(url.getProtocol()).path(this.id.toString()).build().toURL();
        }
        return this.url;
    }

    public Boolean getCompleted(){
        return this.completed == null ? false : this.completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
