package com.aristotle.task.test.spout;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import com.aristotle.task.topology.BoltProcessor;

@Component
public class TestBoltProcessor implements BoltProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        // TODO Auto-generated method stub

    }

    @Override
    public void execute(Tuple input) {
        Date startTime = new Date();
        try {
            // Read the incoming Message
            String message = input.getString(0);
            System.out.println("message Recieved : " + message);
        } catch (Exception ex) {
            // logError("Unable to save lcoation file in redis ", ex);
        } finally {
            Date endTime = new Date();
            // logInfo("Total time taken to process file " + ((endTime.getTime() - startTime.getTime()) / 1000) + " seconds");
        }
    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

}
