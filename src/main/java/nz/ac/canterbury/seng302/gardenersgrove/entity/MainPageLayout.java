package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

/**
 * The main page layout stores how the user has configured their main page (/home)
 */
@Entity
@Table(name = "main_page_layout")
public class MainPageLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "main_page_layout_id")
    private long id;

    /** The gardener to which the layout belongs. */
    @OneToOne
    @JoinColumn(name = "gardener_id")
    private Gardener gardener;

    /** What widget is in the top left container */
    @Column()
    private String widgetSmallOne;

    /** What widget is in the top middle container */
    @Column()
    private String widgetSmallTwo;

    /** What widget is in the right container */
    @Column()
    private String widgetTall;

    /** What widget is in the bottom container */
    @Column()
    private String widgetWide;

    /** What widget is in the bottom container */
    @Column()
    private String format;

    /** What widgets are enabled given as a string of four 1s and 0s (true/false) */
    @Column()
    private String widgetsEnabled;

    protected MainPageLayout() {}

    public MainPageLayout(Gardener gardener) {
        this.gardener = gardener;
        this.format = "1 2 3";
        this.widgetSmallOne = "recentGardens";
        this.widgetSmallTwo = "recentPlants";
        this.widgetWide = "myFriends";
        this.widgetTall = "myGardens";
        this.widgetsEnabled = "1 1 1 1";
    }

    public String getWidgetSmallOne() {
        return widgetSmallOne;
    }

    public void setWidgetSmallOne(String selection) {
        this.widgetSmallOne = selection;
    }

    public String getWidgetSmallTwo() {
        return widgetSmallTwo;
    }

    public void setWidgetSmallTwo(String selection) {
        this.widgetSmallTwo = selection;
    }

    public String getWidgetTall() {
        return widgetTall;
    }

    public void setWidgetTall(String selection) {
        this.widgetTall = selection;
    }

    public String getWidgetWide() {
        return widgetWide;
    }

    public void setWidgetWide(String selection) {
        this.widgetWide = selection;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String selection) {
        this.format = selection;
    }

    public String getWidgetsEnabled() {
        return widgetsEnabled;
    }

    public void setWidgetsEnabled(String widgetsEnabled) {
        this.widgetsEnabled = widgetsEnabled;
    }

}
