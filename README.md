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
-짧은함수 vs 긴함수  

함수가 길 수록 더 이해하기 어렵다 vs 짧은 함수는 더 많은 문맥전환을 필요로 한다.  
작은함수에 "좋은 이름"을 사용했다면 해당 함수의 코드를 보지 않고도 이해할 수 있다.  
어떤 코드에 "주석"을 남기고 싶다면, 주석 대신 함수를 만들고 함수의 이름으로 "의도"를 표현하면 된다.  

-사용할 수 있는 리팩토링 기술  

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


#### 3. 객체 통째로 넘기기
하나의 객체에서 구할 수 있는 여러 값들을 함수에 전달하는 경우, 해당 매개변수를 객체 하나로 교체할 수 있다.
이로써 매개변수 목록을 줄일 수 있다.

```
-- old
private String getMarkdownForParticipant(int totalNumberOfEvents, Participant p) {
	String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), 
					checkMark(p, totalNumberOfEvents), getRate(totalNumberOfEvents, p));
	return markdownForHomework;
}

-- new
private String getMarkdownForParticipant(Participant participant) { // 객체를 통째로 넘긴다.
	return String.format("| %s %s | %.2f%% |\n", participant.username(),
                checkMark(participant.homework(), this.totalNumberOfEvents),
		participant.getRate(this.totalNumberOfEvents);
}

```



#### 4. 함수를 명령으로 바꾸기  
함수를 독립적인 객체인, Command로 만들어 사용할 수 있다.  
커맨드 패턴을 적용하면 다음과 같은 장점을 취할 수 있다.  
- 부가적인 기능으로 undo 기능을 만들 수도 있다.  
- 더 복잡한 기능을 구현하는데 필요한 여러 메소드를 추가할 수 있다.  
- 상속이나 템플릿을 활용할 수도 있다.  
- 복잡한 메소드를 여러 메소드나 필드를 활용해 쪼갤 수도 있다.  


#### 5. 조건문 분해하기  
여러 조건에 따라 달라지는 코드를 작성하다 보면 함수가 길어지게 된다.  
"조건"과 "액션" 모두 "의도"를 표현해야한다.  
"함수 추출하기"와 동일한 리팩토링이지만 의도만 다를 뿐이다.  

#### 6. 반복문 쪼개기
반복문을 여러개로 쪼개면 보다 쉽게 이해하고 수정할 수 있다.
```
# 반복문 쪼개기
-- old
for (int index = 1 ; index <= totalNumberOfEvents ; index++) {
	int eventId = index;
	service.execute(new Runnable() {
	@Override
	public void run() {
	try {
		GHIssue issue = repository.getIssue(eventId);
		List<GHIssueComment> comments = issue.getComments();

		for (GHIssueComment comment : comments) {
			Participant participant = findParticipant(comment.getUserName(), participants);
			participant.setHomeworkDone(eventId);
		}
		...
-- new
for (int index = 1 ; index <= totalNumberOfEvents ; index++) {
            int eventId = index;
            service.execute(() -> {
                try {
                    GHIssue issue = repository.getIssue(eventId);
                    checkHomework(issue.getComments(), participants, eventId);
              ...

private void checkHomework(List<GHIssueComment> comments, List<Participant> participants, int eventId) {
        for (GHIssueComment comment : comments) {
            Participant participant = findParticipant(comment.getUserName(), participants);
            participant.setHomeworkDone(eventId);
	}
}
	

# 메소드 추출하기
-- old
private void print() throws IOException, InterruptedException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        List<Participant> participants = new CopyOnWriteArrayList<>();

        ExecutorService service = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(totalNumberOfEvents);
	
	...

-- new
의미를 부여하여 가능한한 함수로 리팩토링함.
private void print() throws IOException, InterruptedException {
        checkGithubIssues(getGhRepository());
	new StudyPrinter(this.totalNumberOfEvents, this.participants, PrinterMode.MARKDOWN).execute();
}

private GHRepository getGhRepository() throws IOException {
	GitHub gitHub = GitHub.connect();
        return gitHub.getRepository("me/live-study");
}
...
```

#### 7. 조건문을 다형성으로 바꾸기  
여러 타입에 따라 각기 다른 로직으로 처리해야 하는 경우에 다형성을 적용해서 조건문을 보다 명확하게 분리할 수 있다.  
공통으로 사용되는 로직은 상위 클래스에 두고 달라지는 부분만 하위 클래스에 둠으로써, 달라지는 부분만 강조할 수 있다.  

</details>




<details markdown="4">
<summary> 4. 긴 매개변수 목록 </summary>    

어떤 함수에 매개변수가 많을수록 함수의 역할을 이해하기 힘들어진다.  
- 과연 그 함수는 한가지 일을 하고 있는게 맞는가?  
- 불필요한 매개변수는 없는가?  
- 하나의 자료구조로 뭉칠 수 있는 매개변수 목록은 없는가?  

#### 1. 매개변수를 질의 함수로 바꾸기  
함수의 매개변수 목록은 함수의 다양성을 대변하며, 짧을수록 이해하기 좋다.  
어떤 한 매개변수를 다른 매개변수를 통해 알아낼 수 있다면 "중복 매개변수"라 생각할 수 있다.  
매개변수에 값을 전달하는 것은 "함수를 호출하는 쪽"의 책임이다. 가능하면 함수를 호출하는 쪽의 책임을 줄이고 함수 내부에서 책임지도록 노력한다.  
  
```
-- old
public double finalPrice() {
	double basePrice = this.quantity * this.itemPrice;
	int discountLevel = this.quantity > 100 ? 2 : 1;
        return this.discountedPrice(basePrice, discountLevel);
}

private double discountedPrice(double basePrice, int discountLevel) {
	return discountLevel == 2 ? basePrice * 0.9 : basePrice * 0.95;
}

-- new
public double finalPrice() {
	double basePrice = this.quantity * this.itemPrice;
	return this.discountedPrice(basePrice);
}

private int discountLevel() {
	return this.quantity > 100 ? 2 : 1;
}

private double discountedPrice(double basePrice) { // 매개변수로 전달할 필요 없이 함수를 호출해 사용.
	return discountLevel() == 2 ? basePrice * 0.9 : basePrice * 0.95;
}
```

#### 2. 플래그 인수 제거하기
플래그는 보통 함수에 매개변수로 전달해서, 함수 내부의 로직을 분기하는데 사용한다.  
플래그를 사용한 함수는 차이를 파악하기 어렵다.  
- bookConcert(customer, false), bookConcert(customer, true)  
- bookConcert(customer), bookConcert(customer)  
조건문 분해하기를 활용할 수 있다.  
 
```
-- old
public LocalDate deliveryDate(Order order, boolean isRush) {
	if (isRush) {
		int deliveryTime = switch (order.getDeliveryState()) {
			case "WA", "CA", "OR" -> 1;
			case "TX", "NY", "FL" -> 2;
			default -> 3;
		};
		return order.getPlacedOn().plusDays(deliveryTime);
        } else {
	...

assertEquals(placedOn.plusDays(1), shipment.deliveryDate(orderFromWA, true));
assertEquals(placedOn.plusDays(2), shipment.deliveryDate(orderFromWA, false));

-- new
플래그성 파라미터를 제거하고 조금 더 코드를 명시적으로 만든다.
public LocalDate regularDeliveryDate(Order order) {
	int deliveryTime = switch (order.getDeliveryState()) {
		case "WA", "CA" -> 2;
		case "OR", "TX", "NY" -> 3;
		default -> 4;
        };
	return order.getPlacedOn().plusDays(deliveryTime);
}

assertEquals(placedOn.plusDays(1), shipment.rushDeliveryDate(orderFromWA));
assertEquals(placedOn.plusDays(2), shipment.regularDeliveryDate(orderFromWA));

```

#### 3. 여러 함수를 클래스로 묶기  

비슷한 매개변수 목록을 여러 함수에서 사용하고 있다면 해당 메소드를 모아서 클래스를 만들 수 있다.  
클래스 내부로 메소드를 옮기고, 데이터를 필드로 만들면 메소드에 전달해야 하는 매개변수 목록도 줄일 수 있다.  


</details>


<details markdown="4">
<summary> 5. 전역 데이터 </summary>    
전역 데이터는 아무곳에서나 변경될 수 있다는 문제가 있다.  
어떤 코드로 인해 값이 바뀐 것인지 파악하기 어렵다.  
클래스 변수(필드)도 비슷한 문제를 겪을 수 있다.  
"변수 캡슐화하기"를 적용해서 접근을 제어하거나 어디서 사용하는지 파악하기 쉽게 만들 수 있다.  
  
#### 1. 변수 캡슐화 하기
메소드는 점진적으로 새로운 메소드로 변경할 수 있으나, 데이터는 한번에 모두 변경해야한다.  
데이터가 사용되는 범위가 클수록 캡슐화를 하는 것이 더 중요해진다.  
- 함수를 사용해서 값을 변경하면 보다 쉽게 검증 로직을 추가하거나 변경에 따르는 후속 작업을 추가하는 것이 편리하다.  
불면 데이터의 경우에는 이런 리팩토링을 적용할 필요가 없다.  

```
-- old
public static Integer targetTemperature = 70;
public static Boolean heating = true;
...
Thermostats.targetTemperature = -1111600;
Thermostats.fahrenheit = false;
..

-- new
private getter/setter를 사용하여 캡슐화 하여 validation, notify 등 후속작업의 편리성을 가져갈 수 있다.

Thermostats.setTargetTemperature(68);
Thermostats.setReadInFahrenheit(false);
...
private static Integer targetTemperature = 70;
private static Boolean heating = true;
...

public static void setHeating(Boolean heating) {
	// TODO validation
        Thermostats.heating = heating;
        // TODO notify
}
...
```
</details>
