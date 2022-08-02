# Study-refactoring

유지보수 업무를 해보며 리팩토링의 중요성을 깨닫게 되었고,  
내가 짠 소스를 꾸준히 개선해 나가며 버그를 줄이고 코드를 깔끔하게 유지하는 실력을 키우고 싶어 학습하게 됨.

<details markdown="1">
<summary> 1. 이해하기 힘든 이름 </summary>    
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
</details>

<details markdown="2">
<summary> 2. 중복 코드 </summary>    

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
</details>


<details markdown="3">
<summary> 3. 긴 함수</summary>
  
  
짧은함수 vs 긴함수  

함수가 길 수록 더 이해하기 어렵다 vs 짧은 함수는 더 많은 문맥전환을 필요로 한다.  
작은함수에 "좋은 이름"을 사용했다면 해당 함수의 코드를 보지 않고도 이해할 수 있다.  
어떤 코드에 "주석"을 남기고 싶다면, 주석 대신 함수를 만들고 함수의 이름으로 "의도"를 표현하면 된다.  

사용할 수 있는 리팩토링 기술  

99%는 "함수 추출하기"로 해결 가능하다.  
함수로 분리하면서 해당 함수로 전달해야 할 매개변수가 많아진다면 다음과 같은 리팩토링을 고려해볼 수 있다.  
"임시 변수를 질의 함수로 바꾸기"  
"매개변수 객체 만들기"  
"객체 통째로 넘기기"  
  
"조건문 분해하기"를 사용해 조건문을 분리할 수 있다.  
같은 조건으로 여러개의 Switch문이 있다면, "조건문을 다형성으로 바꾸기"를 사용할 수 있다.  
반복문 안에서 여러 작업을 하고 있어서 하나의 메소드로 추출하기 어렵다면, "반복문 쪼개기"를 적용할 수 있다.  


#### 1. 임시 변수를 질의 함수로 바꾸기
변수를 사용하면 반복해서 동일한 식을 계산하는 것을 피할 수 있고, 이름을 사용해 의미를 표현할 수도 있다.
긴 함수를 리팩토링할 때, 그러한 임시 변수를 함수로 추출하여 분리한다면 뺴낸 함수로 전달해야 할 매개변수를 줄일 수 있다.

```
-- old
파라미터가 너무 많다고 생각이 될 때, 구현부를 메소드로 빼는 방법이 있다.
participants.forEach(p -> {
    long count = p.homework().values().stream()
            .filter(v -> v == true)
            .count();
    double rate = count * 100 / totalNumberOfEvents;

    // 파라미터가 너무 많다.
    String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), rate);
    writer.print(markdownForHomework);
});

-- new
participants.forEach(p -> {
    String markdownForHomework = getMarkdownForParticipant(totalNumberOfEvents, p); 
    writer.print(markdownForHomework);
});

private double getRate(int totalNumberOfEvents, Participant p) {
    long count = p.homework().values().stream()  
    ...
    return rate;
}

private String getMarkdownForParticipant(int totalNumberOfEvents, Participant p) {
    String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), getRate(totalNumberOfEvents, p));
    return markdownForHomework;
}
```

#### 2. 매개변수 객체 만들기
같은 매개변수들이 여러 메소드에 걸쳐 나타난다면 그 매개변수들을 묶은 자료 구조를 만들 수 있다.  
그렇게 만든 자료구조는 :   
	- 해당 데이터간의 관계를 보다 명시적으로 나타낼 수 있다.  
	- 함수에 전달할 매개변수 개수를 줄일 수 있다.  
	- 도메인을 이해하는데 중요한 역할을 하는 클래스로 발전할 수도 있다.  
  
```
-- old
여러 메소드에서 반복되는 필드(totalNumberOfEvents)가 있다면 전역변수로 적용하는 것도 방법이다.

writer.print(header(totalNumberOfEvents, participants.size()));
...

private double getRate(int totalNumberOfEvents, Participant p) 
...
  
private String getMarkdownForParticipant(int totalNumberOfEvents, Participant p) 
...

-- new

private int totalNumberOfEvents;

public StudyDashboard(int totalNumberOfEvents) {
    this.totalNumberOfEvents = totalNumberOfEvents;
}
...
StudyDashboard studyDashboard = new StudyDashboard(15);
```












</details>
