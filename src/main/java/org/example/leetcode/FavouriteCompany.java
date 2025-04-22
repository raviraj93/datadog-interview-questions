package org.example.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavouriteCompany {
        public List<Integer> peopleIndexes(List<List<String>> favoriteCompanies) {
            Map<String, List<Integer>> companyIndex = getCompanyIndex(favoriteCompanies);
            List<Integer> answer = new ArrayList<>();
            for (int i = 0; i < favoriteCompanies.size(); i++) {
                if (favoriteCompanies.get(i).size() != findMatchingCompanyIndex(i, favoriteCompanies.get(i), companyIndex, favoriteCompanies.size())) {
                    answer.add(i);
                }
            }
            return answer;
        }

        private Map<String, List<Integer>> getCompanyIndex(List<List<String>> favoriteCompanies) {
            Map<String, List<Integer>> companyIndex = new HashMap<>();
            for (int i = 0; i < favoriteCompanies.size(); i++) {
                for (String company : favoriteCompanies.get(i)) {
                    companyIndex.computeIfAbsent(company, l -> new ArrayList<>()).add(i);
                }
            }
            System.out.println("companyIndex: " + companyIndex);
            return companyIndex;
        }

        private int findMatchingCompanyIndex(int person, List<String> companies, Map<String, List<Integer>> companyIndex, int maxPeople) {
            int[] peopleCount = new int[maxPeople];
            int maxMatch = 0;
            System.out.println("Checking person: " + person);
            for (String company : companies) {
                for (int favPersonCompany : companyIndex.get(company)) {
                    if (favPersonCompany != person) {
                        peopleCount[favPersonCompany]++;
                        maxMatch = Math.max(maxMatch, peopleCount[favPersonCompany]);
                    }
                }
            }
            return maxMatch;
        }


        public List<Integer> peopleIndexes1(List<List<String>> favoriteCompanies) {
            List<Integer> answer = new ArrayList<>();
            for(int i=0;i<favoriteCompanies.size();i++) {
                boolean isCommon = false;
                for(int j=0;j<favoriteCompanies.size();j++) {
                    if(i==j) {
                        continue;
                    }
                    Set<String> a = new HashSet<>(favoriteCompanies.get(i));
                    Set<String> b = new HashSet<>(favoriteCompanies.get(j));
                    a.removeAll(b);
                    if(a.isEmpty()) {
                        isCommon = true;
                        break;
                    }
                }
                if(!isCommon) {
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
