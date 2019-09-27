package com.example.motoinventoryservice.controller;

import com.example.motoinventoryservice.dao.MotoInventoryDao;
import com.example.motoinventoryservice.model.Motorcycle;
import com.example.motoinventoryservice.util.feign.VinLookupClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class MotoInventoryController {

    @Autowired
    MotoInventoryDao motoDao;

    private List<Motorcycle> motorcycles = new ArrayList<>();

    @RequestMapping(value = "/motorcycles", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Motorcycle createMotorcycle(@RequestBody @Valid Motorcycle motorcycle) {
        return motoDao.addMotorcycle(motorcycle);
    }

    @RequestMapping(value = "/motorcycles", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<Motorcycle> getAllMotorcycle(){
        return motoDao.getAllMotorcycles();
    }

    @RequestMapping(value = "/motorcycles/{motoId}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Motorcycle getMotorcycle(@PathVariable int motoId) {
        if (motoId < 1) {
            throw new IllegalArgumentException("MotoId must be greater than 0.");
        }

        return motoDao.getMotorcycle(motoId);
    }

    @RequestMapping(value = "/motorcycles/make/{make}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<Motorcycle> getMotorcycleByMake(@PathVariable String make){
        return motoDao.getMotorcyclesByMake(make);
    }

    @RequestMapping(value = "/motorcycles/{motoId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMotorcycle(@PathVariable("motoId") int motoId) {
        motoDao.deleteMotorcycle(motoId);
    }

    @RequestMapping(value = "/motorcycles/{motoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMotorcycle(@RequestBody @Valid Motorcycle motorcycle, @PathVariable int motoId) {
        // make sure the motoId on the path matches the id of the motorcycle object
        if (motoId != motorcycle.getId()) {
            throw new IllegalArgumentException("Motorcycle ID on path must match the ID in the Motorcycle object.");
        }

        // do nothing here - in a real application we would update the entry in the backing data store

    }

    @Autowired
    private final VinLookupClient client;

    MotoInventoryController(VinLookupClient client){
        this.client = client;
    }

    @RequestMapping(value = "/vehicle/{vin}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, Object> getVehicleInformation(@PathVariable("vin") String vin){
        HashMap<String, Object> vehicle = (HashMap<String, Object>) client.lookUpVehicle(vin);
        return vehicle;
    }
}
