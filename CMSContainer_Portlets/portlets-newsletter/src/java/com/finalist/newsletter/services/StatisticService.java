package com.finalist.newsletter.services;

import java.util.List;

import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;

public interface StatisticService {

   public List<StatisticResult> statisticAll();

   public List<StatisticResult> statisticByNewsletter(int newsletterId);

   public List<StatisticResult> statisticAllByPeriod(String start, String end) throws ServiceException;

   public StatisticResult statisticByNewsletterPeriod(int newsletterId, String start, String end) throws ServiceException;

   public StatisticResult statisticSummery();

   public StatisticResult statisticSummeryPeriod(String start, String end) throws ServiceException;

   public StatisticResult statisticSummaryByNewsletter(int newsletterId);

   public List<StatisticResult> statisticDetailByNewsletterPeriod(int newsletterId, String start, String end) throws ServiceException;

   public void logPubliction(int newsletterId, HANDLE handle);

   public int pushSummedLogs(List<StatisticResult> listRecorder);

   public List<StatisticResult> getLogs();

}
