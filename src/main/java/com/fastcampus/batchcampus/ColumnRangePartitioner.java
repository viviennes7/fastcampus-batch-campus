package com.fastcampus.batchcampus;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ColumnRangePartitioner implements Partitioner {

    private final JdbcTemplate jdbcTemplate;

    public ColumnRangePartitioner(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) { // 5
        final Integer min = jdbcTemplate.queryForObject("SELECT MIN(id) from USER", Integer.class); // 1
        final Integer max = jdbcTemplate.queryForObject("SELECT MAX(id) from USER", Integer.class); // 100
        int targetSize = (max - min) / gridSize + 1; // 20

        final Map<String, ExecutionContext> result = new HashMap<>();
        int number = 0;
        int start = min;    //1
        int end = start + targetSize - 1;   // 20

        while (start <= max) {
            final ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);

            if (end >= max) {
                end = max;
            }

            value.putInt("minValue", start);
            value.putInt("maxValue", end);

            start += targetSize;
            end += targetSize;
            number++;

        }

        return result;
    }

    // partition0 1, 20
    // partition1 21, 40
    // partition1 41, 60
    // partition1 61, 80
    // partition1 81, 100

}
