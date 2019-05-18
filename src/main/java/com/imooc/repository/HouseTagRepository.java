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

    List<HouseTag> findAllByHouseIdIn(List<Integer> houseIds);
}
