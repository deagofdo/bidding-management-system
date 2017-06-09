package com.biddie.repo;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.biddie.entity.Items;

import java.util.Date;
import java.util.List;

import javax.persistence.NamedQuery;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.repository.CrudRepository;
@Component
@Repository
public interface Inventory extends JpaRepository<Items, String> {
@Query("from Inventory i  where  Start_Auction<=:date and End_Auction>=:date" )
public List<Items> availableBid(@Param ("date") String date);
	@Query("select basePrice from Inventory where itemNo=:itemno" )
	public String getBaseBid(@Param ("itemno") String itemno);
}
//findBy