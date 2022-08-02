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

## 2. 중복 코드  
- 중복코드의 단점
비슷한지, 완전히 동일한 코드인지 주의 깊게 봐야한다.
코드를 변경할 때, 동일한 모든 곳의 코드를 변경해야 한다.

- 사용할 수 있는 리팩토링 기술  
동일한 코드를 여러 메소드에서 사용하는 경우, 함수 추출하기 (Extract Function)  
코드가 비슷하게 생겼지만 완전히 같지는 않은 경우, 코드 분리하기 (Slide Statements)  
여러 하위 클래스에 동일한 코드가 있다면, 메소드 올리기 (Pull Up Method)   

#### 1. 필드 이름 바꾸기
"의도"와 "구현" 분리하기  
무슨 일을 하는 코드인지 알아내려고 노력해야 하는 코드라면 해당 코드를 함수로 분리하고 함수 이름으로 "무슨 일을 하는지" 표현할 수 있다.  
거대한 함수 안에 들어있는 주석은 추출한 함수를 찾는데 있어서 좋은 단서가 될 수 있다.  
```
-- old
GitHub gitHub = GitHub.connect();
GHRepository repository = gitHub.getRepository("me/live-study");
GHIssue issue = repository.getIssue(30);

-- new
GHIssue issue = getGhIssue(30); 
...
private GHIssue getGhIssue(int eventId) { // 함수 이름으로 표현
    GitHub gitHub = GitHub.connect();
    GHRepository repository = gitHub.getRepository("me/live-study");
    GHIssue issue = repository.getIssue(eventId);
    return issue;
}
```

#### 2. 코드 정리하기  
관련있는 코드끼리 묶여있어야 코드를 더 쉽게 이해할 수 있다.  
함수에서 사용할 변수를 상단에 미리 정의하기 보다는, 해당 변수를 사용하는 코드 바로 위에 선언하자.  
관련있는 코드끼리 묶은 다음, 함수 추출하기를 사용해서 더 깔끔하게 분리할 수도 있다.  
```
-- old
Set<String> reviewers = new HashSet<>();
GitHub gitHub = GitHub.connect();
...
issue.getComments().forEach(c -> reviewers.add(c.getUserName()));
        
-- new
Set<String> reviewers = new HashSet<>(); // 해당 변수를 사용하는 코드 바로 위에 선언.
issue.getComments().forEach(c -> reviewers.add(c.getUserName()));
```

#### 3. 메소드 올리기
중복 코드는 당장은 잘 동작하더라도 미래에 버그를 만들어 낼 빌미를 제공한다.  
예) A에서 코드를 고치고, B에는 반영하지 않은 경우  
비슷하지만 일부 값만 다른 경우라면, "함수 매개변수화 하기" 리팩토링을 적용한 이후에, 이 방법을 사용할 수 있다.  

