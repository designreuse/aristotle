package com.aristotle.core.persistance.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aristotle.core.persistance.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    /*
    public Candidate getCandidateByPcIdAndElectionId(Long pcId, Long electionId);
	
    public Candidate getCandidateByAcIdAndElectionId(Long acId, Long electionId);

    public Candidate getCandidateByExtPcId(String pcId);
	
    public Candidate getCandidateByExtAcId(String acId);

    public List<Candidate> getAllCandidates(int totalItems, int pageNumber);
	
    public List<Candidate> getAllCandidates();
	
    public List<Candidate> getAllCandidatesByElectionId(Long electionId);

    public List<Candidate> getCandidateItemsAfterId(long candidateId);
    */
}
