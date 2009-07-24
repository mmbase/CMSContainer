package com.finalist.newsletter.cao;

import java.util.Date;
import java.util.List;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;

public interface NewsLetterStatisticCAO {
   public List<StatisticResult> getAllRecords();

   public List<StatisticResult> getRecordsByNewsletter(int newsletter);

   public List<StatisticResult> getAllRecordsByPeriod(Date start, Date end);

   public List<StatisticResult> getRecordsByNewsletterAndPeriod(Date startDate, Date endDate, int newsletterId);

   public void logPublication(int userId, int newsletterId, HANDLE handle);

   /**
    * @param listRecorder
    *           which get from data
    * @return how many SummedLogs to insert
    */
   public int insertSummedLogs(List<StatisticResult> listRecorder);

   /**
    * @return List which sumLogs about StatisticResult
    */
   public List<StatisticResult> getLogs();
   
}