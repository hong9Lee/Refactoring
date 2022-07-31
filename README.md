# Study-refactoring

유지보수 업무를 해보며 리팩토링의 중요성을 깨닫게 되었고,  
내가 짠 소스를 꾸준히 개선해 나가며 버그를 줄이고 코드를 깔끔하게 유지하는 실력을 키우고 싶어 학습하게 됨.

## 1. 이해하기 힘든 이름  
깔끔한 코드에서 가장 중요한 것 중 하나가 바로 "좋은 이름"이다.
역할에 대해 직관적이여야한다.

#### 1. 함수 선언 변경하기

좋은 이름을 찾아내는 방법  
-> 좋은 이름을 가진 함수는 소스를 보지 않고 이름만 보고도 이해할 수 있다.   
-> 함수에 주석을 작성한 다음, 주석을 함수 이름으로 만들어 본다.

```
--old
private void studyReviews(GHIssue issue) throws IOException {
    List<GHIssueComment> comments = issue.getComments();
    for (GHIssueComment comment : comments) {
      usernames.add(comment.getUserName());
      reviews.add(comment.getBody());
    }
}
    
    
--new
/**
 * 스터디 리뷰 이슈에 작성되어 있는 리뷰어 목록과 리뷰를 읽어온다.
 */
private void loadReviews(GHIssue issue) throws IOException {
  List<GHIssueComment> comments = issue.getComments();
  for (GHIssueComment comment : comments) {
    usernames.add(comment.getUserName());
    reviews.add(comment.getBody());
  }
}
```

#### 2. 변수 이름 변경하기
더 많이 사용되는 변수일수록 그 이름이 더 중요하다.
```
-- old
List<GHIssueComment> comments = issue.getComments();
for (GHIssueComment comment : comments) {
  usernames.add(comment.getUserName());
  this.reviews.add(comment.getBody());
}

-- new
/**
 * 리뷰를 읽어오는 함수이기 떄문에 좀 더 구체적인 네이밍
*/
List<GHIssueComment> reviews = issue.getComments();
for (GHIssueComment review : reviews) {
  usernames.add(review.getUserName());
  this.reviews.add(review.getBody());
}
```

#### 3. 필드 이름 바꾸기
```
-- old
private Set<String> usernames = new HashSet<>();

-- new
/**
 * 리뷰어들의 이름이기에 좀 더 구체적인 네이밍
*/
private Set<String> reviewers = new HashSet<>();
```
