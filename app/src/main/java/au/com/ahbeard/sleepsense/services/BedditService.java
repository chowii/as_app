package au.com.ahbeard.sleepsense.services;

import com.beddit.analysis.AnalysisException;
import com.beddit.analysis.BatchAnalysis;
import com.beddit.analysis.BatchAnalysisContext;
import com.beddit.analysis.BatchAnalysisResult;
import com.beddit.analysis.CalendarDate;
import com.beddit.analysis.SessionData;
import com.beddit.analysis.TimeValueFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by neal on 10/03/2016.
 */
public class BedditService {

    BatchAnalysis mBatchAnalysis;

    public void performBatchAnalysis() {

        TimeValueFragment timeValueFragment = new TimeValueFragment();

        List<SessionData> sessionDatas = new ArrayList<>();
        SessionData sessionData = new SessionData(0,0,timeValueFragment);
        sessionDatas.add(sessionData);

        List<BatchAnalysisResult> priorBatchAnalysisResults = new ArrayList<>();
        BatchAnalysisResult priorBatchAnalysisResult = new BatchAnalysisResult(0,0,new CalendarDate(1999,10,10));
        priorBatchAnalysisResults.add(priorBatchAnalysisResult);

        BatchAnalysisContext batchAnalysisContext = new BatchAnalysisContext(100);

        try {
            mBatchAnalysis = new BatchAnalysis();
            BatchAnalysisResult batchAnalysisResult = mBatchAnalysis.analyzeSessions(
                    sessionDatas,priorBatchAnalysisResults,new CalendarDate(1999,10,10),batchAnalysisContext);
        } catch (AnalysisException e) {
            e.printStackTrace();
        }

    }


}
