import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Engine implements Runnable {
    private static final String BASE_URL = "https://www.vfu.bg/ucheben_razpis/files/";
    private static final String FOURTH_COURSE = "/inf_4.pdf";
    private static final String SECOND_COURSE = "/inf_2.pdf";
    private static final String LECTURES_HTML = "http://localhost:63342/Lecturer/resources/lectures.html";

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        fetchFourthCourse();
        fetchSecondCourse();
        renderHtml();
    }

    /**
     * Method to fetch the lectures information about 4th course in a pdf format
     */
    private static void fetchFourthCourse(){
        String urlToFetch = BASE_URL + calcWeekScope() + FOURTH_COURSE;
        fetchByUrl(urlToFetch);
    }

    /**
     * Method to fetch the lectures information about 2nd course in a pdf format
     */
    private static void fetchSecondCourse() {
        String urlToFetch = BASE_URL + calcWeekScope() + SECOND_COURSE;
        fetchByUrl(urlToFetch);
    }

    /**
     * Method to calculate the startWeekDate and endWeekDate
     * @return String the string which we use to determine the span of the week
     */
    private static String calcWeekScope(){
        LocalDateTime currentWeekDate = LocalDateTime.now();
        LocalDateTime lastWeekDate = currentWeekDate;
        int dayOfWeek = currentWeekDate.getDayOfWeek().getValue();

        switch (dayOfWeek){
            case 1:
                lastWeekDate = currentWeekDate.plusDays(6);
                break;
            case 2:
                lastWeekDate = currentWeekDate.plusDays(5);
                currentWeekDate = currentWeekDate.minusDays(1);
                break;
            case 3:
                lastWeekDate = currentWeekDate.plusDays(4);
                currentWeekDate = currentWeekDate.minusDays(2);
                break;
            case 4:
                lastWeekDate = currentWeekDate.plusDays(3);
                currentWeekDate = currentWeekDate.minusDays(3);
                break;
            case 5:
                lastWeekDate = currentWeekDate.plusDays(2);
                currentWeekDate = currentWeekDate.minusDays(4);
                break;
            case 6:
                lastWeekDate = currentWeekDate.plusDays(1);
                currentWeekDate = currentWeekDate.minusDays(5);
                break;
            case 7:
                currentWeekDate = currentWeekDate.minusDays(6);
                break;
        }

        String formatCurrentWeekDate = currentWeekDate.format(DateTimeFormatter.ofPattern("dd.MM.", Locale.ENGLISH));
        String formatLastWeekDate = lastWeekDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH));

        return String.format("%s-%s", formatCurrentWeekDate, formatLastWeekDate);
    }

    /**
     * Method for fetching information from a given url String
     * @param urlToFetch the url String to fetch
     */
    private static void fetchByUrl(String urlToFetch){
        URL url = null;
        try {
            url = new URL(urlToFetch);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        if (url != null){
            try (InputStream  in = url.openStream()) {
                if (urlToFetch.contains(SECOND_COURSE)){
                    Files.copy(in, Paths.get("lectures_2nd_course.pdf"), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Success when fetching 2nd course lectures!");
                } else if (urlToFetch.contains(FOURTH_COURSE)){
                    Files.copy(in, Paths.get("lectures_4th_course.pdf"), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Success when fetching 4th course lectures!");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Url is null.");
        }
    }

    /**
     * Method that renders the html with the two courses information
     */
    private static void renderHtml(){
        try {
            Desktop.getDesktop().browse(new URI(LECTURES_HTML));
        } catch (IOException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }
    }
}
