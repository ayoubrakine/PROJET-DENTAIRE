package my.app.projetdentaire.beans;

import java.util.List;

public class PW {

    private Long id ;
    private String title ;
    private String objectif ;
    private String doc ;

    public PW() {
    }

    public PW(Long id, String title, String objectif, String doc) {
        this.id = id;
        this.title = title;
        this.objectif = objectif;
        this.doc = doc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public String getDocs() {
        return doc;
    }

    public void setDocs(String docs) {
        this.doc = docs;
    }
}
