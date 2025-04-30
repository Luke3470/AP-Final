package UI;

import java.util.ArrayList;
import java.util.List;

public class ChooChooPlaneFlightModel {

    private int pagination = 25;
    private int page = 1;
    private int maxPage = 10;

    // You can expand this to hold search results or database references in the future
    private List<String[]> searchResults;

    public ChooChooPlaneFlightModel() {
        searchResults = new ArrayList<>();
    }

    // Pagination logic
    public int getPagination() {
        return pagination;
    }

    public void setPagination(int pagination) {
        this.pagination = pagination;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    // Placeholder: Add search result logic later
    public List<String[]> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<String[]> searchResults) {
        this.searchResults = searchResults;
    }
}

