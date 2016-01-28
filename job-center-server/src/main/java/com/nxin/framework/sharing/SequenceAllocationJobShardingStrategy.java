package com.nxin.framework.sharing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by petzold on 2015/10/17.
 */
public class SequenceAllocationJobShardingStrategy implements IJobShardingStrategy
{
    @Override
    public Map<String, List<Integer>> sharding(List<String> serversList, int shardingTotalCount)
    {
        if(serversList.size() > shardingTotalCount)
        {
            Map<String,List<Integer>> map = Maps.newHashMapWithExpectedSize(shardingTotalCount);
            for (int i=0;i<shardingTotalCount;i++)
            {
                map.put(serversList.get(i), Lists.newArrayList(i+1));
            }
            return map;
        }
        int a = shardingTotalCount / serversList.size();
        int b = shardingTotalCount % serversList.size();
        Map<String,List<Integer>> map = Maps.newHashMapWithExpectedSize(serversList.size());
        int k=1;
        for (int i=0;i<serversList.size();i++)
        {
            List<Integer> ls = Lists.newArrayList();
            for (int j=0;j<a;j++)
            {
                ls.add(k);
                k++;
            }
            if(i<b)
            {
                ls.add(k);
                k++;
            }
            map.put(serversList.get(i),ls);
        }
        return map;
    }
}
