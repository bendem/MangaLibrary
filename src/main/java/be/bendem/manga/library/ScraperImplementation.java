package be.bendem.manga.library;

import be.bendem.manga.scraper.Scraper;
import be.bendem.manga.scraper.implementations.MangaEdenScraper;
import be.bendem.manga.scraper.implementations.MangaFoxScraper;
import be.bendem.manga.scraper.implementations.MangaReaderScraper;
import be.bendem.manga.scraper.implementations.MangaTownScraper;

public enum ScraperImplementation {

    MangaEden(MangaEdenScraper.class),
    MangaFox(MangaFoxScraper.class),
    MangaReader(MangaReaderScraper.class),
    MangaTown(MangaTownScraper.class),
    ;

    private final Class<? extends Scraper> scraperClass;

    ScraperImplementation(Class<? extends Scraper> scraperClass) {
        this.scraperClass = scraperClass;
    }

    public Class<? extends Scraper> getScraperClass() {
        return scraperClass;
    }

}
