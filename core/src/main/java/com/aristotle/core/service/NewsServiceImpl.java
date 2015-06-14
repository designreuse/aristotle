package com.aristotle.core.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aristotle.core.enums.ContentStatus;
import com.aristotle.core.exception.AppException;
import com.aristotle.core.persistance.ContentTweet;
import com.aristotle.core.persistance.Location;
import com.aristotle.core.persistance.News;
import com.aristotle.core.persistance.repo.LocationRepository;
import com.aristotle.core.persistance.repo.NewsRepository;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public News publishNews(Long newsId) {
        News news = newsRepository.findOne(newsId);
        news.setContentStatus(ContentStatus.Published);
        if (news.getPublishDate() == null) {
            news.setPublishDate(new Date());
        }
        news = newsRepository.save(news);
        return news;
    }

    @Override
    public News rejectNews(Long newsId, String reason) {
        News news = newsRepository.findOne(newsId);
        news.setContentStatus(ContentStatus.Rejected);
        news = newsRepository.save(news);
        return news;
    }

    @Override
    public News getNewsByOriginalUrl(String originalUrl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public News getNewsById(Long newsId) {
        return newsRepository.findOne(newsId);
    }

    @Override
    public List<News> getAllPublishedNews(int totalNews) {
        Pageable pageable = new PageRequest(0, totalNews);
        return newsRepository.getGlobalNews(pageable);
    }

    @Override
    public News saveNews(News news, List<ContentTweet> contentTweetDtos, Long locationId) {
        news = newsRepository.save(news);

        addLocationToNews(news, locationId);
        return news;
    }

    private void addLocationToNews(News news, Long locationId) {
        if (locationId == null || locationId <= 0) {
            return;
        }
        Location location = locationRepository.findOne(locationId);
        if (location == null) {
            return;
        }
        if (news.getLocations() == null) {
            news.setLocations(new HashSet<Location>());
        }
        news.getLocations().add(location);
    }

    @Override
    public List<News> getAllGlobalNews() throws AppException {
        Pageable pageable = new PageRequest(0, 100);
        return newsRepository.getGlobalNews(pageable);
    }

    @Override
    public List<ContentTweet> getNewsContentTweets(Long newsId) throws AppException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<News> getAllLocationPublishedNews(Set<Long> locationIds, int pageNumber, int pageSize) throws AppException {
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        if (locationIds == null || locationIds.isEmpty()) {
            return newsRepository.getGlobalPublishdNews(pageable);
        }
        return newsRepository.getLocationPublishedNews(locationIds, pageable);
    }

}