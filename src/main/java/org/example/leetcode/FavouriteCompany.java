package org.example.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavouriteCompany {
    // Main method to return list of people whose favorite companies are not a subset of others

    public List<Integer> peopleIndexes(List<List<String>> favoriteCompanies) {
        // Map each company to the list of people who like it

        Map<String, List<Integer>> companyToPeopleMap = mapCompaniesToPeople(favoriteCompanies);
        List<Integer> result = new ArrayList<>();

        for (int person = 0; person < favoriteCompanies.size(); person++) {
            // If this person's list is not a subset of any other person's list, include them in the result
            if (!isSubsetOfAnother(person, favoriteCompanies, companyToPeopleMap)) {
                result.add(person);
            }
        }

        return result;
    }

    private Map<String, List<Integer>> mapCompaniesToPeople(List<List<String>> favoriteCompanies) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < favoriteCompanies.size(); i++) {
            for (String company : favoriteCompanies.get(i)) {
                map.computeIfAbsent(company, k -> new ArrayList<>()).add(i);
            }
        }
        return map;
    }

    // Check if the given person's list is a subset of any other person's list
    private boolean isSubsetOfAnother(int personIndex, List<List<String>> favoriteCompanies, Map<String, List<Integer>> companyToPeopleMap) {
        int[] sharedCounts = new int[favoriteCompanies.size()];  // Count of shared companies with each person
        int totalCompanies = favoriteCompanies.get(personIndex).size();  // Number of companies person likes

        // For each company this person likes, increment count for others who also like it
        for (String company : favoriteCompanies.get(personIndex)) {
            for (int otherPerson : companyToPeopleMap.get(company)) {
                if (otherPerson != personIndex) {
                    sharedCounts[otherPerson]++;
                }
            }
        }

        // If any other person shares **all** the companies → it's a subset
        for (int sharedCount : sharedCounts) {
            if (sharedCount == totalCompanies) {
                return true;
            }
        }

        return false;  // Not a subset of any other
    }


    public List<Integer> peopleIndexes1(List<List<String>> favoriteCompanies) {
        List<Integer> answer = new ArrayList<>();
        for (int i = 0; i < favoriteCompanies.size(); i++) {
            boolean isCommon = false;
            for (int j = 0; j < favoriteCompanies.size(); j++) {
                if (i == j) {
                    continue;
                }
                Set<String> a = new HashSet<>(favoriteCompanies.get(i));
                Set<String> b = new HashSet<>(favoriteCompanies.get(j));
                a.removeAll(b);
                if (a.isEmpty()) {
                    isCommon = true;
                    break;
                }
            }
            if (!isCommon) {
                answer.add(i);
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        List<List<String>> favoriteCompanies = List.of(
                List.of("leetcode", "google", "facebook"),
                List.of("google", "microsoft"),
                List.of("google", "facebook"),
                List.of("google"),
                List.of("amazon")
        );
        FavouriteCompany favouriteCompany = new FavouriteCompany();
        System.out.println(favouriteCompany.peopleIndexes(favoriteCompanies));
    }

}
