package com.imooc.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.imooc.entity.HouseTag;

/**
 * Created by 瓦力.
 */
public interface HouseTagRepository extends CrudRepository<HouseTag, Integer> {
    HouseTag findByNameAndHouseId(String name, int houseId);

    List<HouseTag> findAllByHouseId(int id);


	/**   
	 * @Title: findAllByHouseIdIn   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param houseIds
	 * @param: @return      
	 * @return: List<HouseTag>      
	 * @throws   
	 */
	List<HouseTag> findAllByHouseIdIn(List<Integer> houseIds);
}
