package com.adriansng.projet_7_go4lunch;

import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.repositories.PlacesRepository;
import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class CalculUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private Workmate workmateA, workmateB, workmateC;

    private static final PlacesRepository placeRepository = new PlacesRepository();
    private static final WorkmatesRepository workmateRepository = new WorkmatesRepository();


    @Before
    public void setUp() {
        // Workmates
        when(workmateA.getUid()).thenReturn("A");
        when(workmateB.getUid()).thenReturn("B");
        when(workmateC.getUid()).thenReturn("C");
        when(workmateA.getChooseRestaurant()).thenReturn("RestaurantA");
        when(workmateB.getChooseRestaurant()).thenReturn("RestaurantB");
        when(workmateC.getChooseRestaurant()).thenReturn("RestaurantB");
    }


    //-------------------
    // PLACE REPOSITORY
    //-------------------

    @Test
    public void getRating_isCorrect() {
        Double note = 3.5;
        Double noteOn4 = 2.8;
        Double noteCalculation = placeRepository.getRating(note);
        assertEquals(noteCalculation, noteOn4);
    }

    @Test
    public void checkIsFavorite_isCorrect() {
        List<String> listFavorite = new ArrayList<>();
        listFavorite.add("A");
        listFavorite.add("B");
        assertTrue(placeRepository.checkIsFavorite("A", listFavorite));
        assertFalse(placeRepository.checkIsFavorite("C", listFavorite));
    }

    @Test
    public void checkHowManyWorkmate_isCorrect() {
        List<Workmate> workmates = new ArrayList<>();
        workmates.add(workmateA);
        workmates.add(workmateB);
        workmates.add(workmateC);
        int restaurantA = placeRepository.checkHowManyWorkmate("RestaurantA", workmates);
        int restaurantB = placeRepository.checkHowManyWorkmate("RestaurantB", workmates);
        int restaurantC = placeRepository.checkHowManyWorkmate("RestaurantC", workmates);
        assertEquals(restaurantA, 1);
        assertEquals(restaurantB, 2);
        assertEquals(restaurantC, 0);
    }

    //-------------------
    // WORKMATE REPOSITORY
    //-------------------

    @Test
    public void checkWorkmatesForTheRestaurant_isCorrect() {
        List<Workmate> workmates = new ArrayList<>();
        workmates.add(workmateA);
        workmates.add(workmateB);
        workmates.add(workmateC);
        assertTrue(workmateRepository.checkWorkmatesForTheRestaurant(workmates, "RestaurantA"));
        assertFalse(workmateRepository.checkWorkmatesForTheRestaurant(workmates, "RestaurantC"));
    }


    @Test
    public void checkNumberWorkmateChoose_isCorrect() {
        List<Workmate> workmatesA = new ArrayList<>();
        workmatesA.add(workmateA);
        workmatesA.add(workmateB);
        workmatesA.add(workmateC);
        List<Workmate> workmatesB = new ArrayList<>();
        workmatesB.add(workmateA);
        workmatesB.add(workmateB);
        assertTrue(workmateRepository.checkNumberWorkmateChoose(workmatesA, workmatesB));
        assertFalse(workmateRepository.checkNumberWorkmateChoose(workmatesA, workmatesA));
    }

    @Test
    public void getNumberWorkmateChoose_isCorrect() {
        List<Workmate> workmatesA = new ArrayList<>();
        workmatesA.add(workmateA);
        workmatesA.add(workmateB);
        workmatesA.add(workmateC);
        List<Workmate> workmatesB = new ArrayList<>();
        workmatesB.add(workmateA);
        workmatesB.add(workmateB);
        assertEquals(3, workmateRepository.getNumberWorkmateChoose(workmatesA));
        assertEquals(2, workmateRepository.getNumberWorkmateChoose(workmatesB));
    }

    @Test
    public void checkWorkmateIsToExist_isCorrect() {
        List<Workmate> workmates = new ArrayList<>();
        workmates.add(workmateA);
        workmates.add(workmateB);
        assertTrue(workmateRepository.checkWorkmateIsToExist(workmateA.getUid(), workmates));
        assertFalse(workmateRepository.checkWorkmateIsToExist(workmateC.getUid(), workmates));
    }

    @Test
    public void checkRestaurantIsValidate_isCorrect() {
        assertTrue(workmateRepository.checkRestaurantIsValidate("A", "A"));
        assertFalse(workmateRepository.checkRestaurantIsValidate("A", "B"));
    }

    @Test
    public void checkRestaurantIsFavorite_isCorrect() {
        List<String> listFavorite = new ArrayList<>();
        listFavorite.add("A");
        listFavorite.add("B");
        assertTrue(workmateRepository.checkRestaurantIsFavorite(listFavorite, "A"));
        assertFalse(workmateRepository.checkRestaurantIsFavorite(listFavorite, "C"));
    }
}