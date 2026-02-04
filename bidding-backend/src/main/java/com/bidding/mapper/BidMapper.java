package com.bidding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bidding.entity.BidRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BidMapper extends BaseMapper<BidRecord> {
}
