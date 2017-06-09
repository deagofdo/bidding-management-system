package com.biddie.repo;

import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.biddie.entity.BidList;
@Component
@Repository
public interface BidRepo extends CrudRepository<BidList, String>{
//public List<BidList> findByItem_no(String itemno);
	//@OrderBy("item_no DESC")
	@Query("select b from BidList b where b.item_no=:itemno ORDER BY b.bid DESC" )
	public List<BidList> highestbid(@Param("itemno")String itemno);
	@Query("select b from BidList b where b.item_no=:itemno")
	public List<BidList> itemExist(@Param("itemno")String itemno);
	
}
