package com.nxin.framework.sharing;

import java.util.List;
import java.util.Map;

/**
 * Created by petzold on 2015/10/17.
 */
public interface IJobShardingStrategy
{
    Map<String, List<Integer>> sharding(List<String> serversList, int shardingTotalCount);
}
