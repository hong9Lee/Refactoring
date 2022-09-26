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


<details markdown="5">
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



<details markdown="6">
<summary> 6. 가변 데이터 </summary>    
데이터를 변경하다보면 예상치 못했던 결과나 해결하기 어려운 버그가 발생하기도 한다.
함수형 프로그래밍 언어는 데이터를 변경하지 않고 복사본을 전달한다.
하지만 그 밖의 프로그래밍 언어는 데이터 변경을 허용하고 있다.
따라서 변경되는 데이터 사용 시 발생할 수 있는 리스크를 관리할 수 있는 방법을 적용하는 것이 좋다.

#### 1.변수 쪼개기
어떤 변수가 여러번 재할당 되어도 적절한 경우  
- 반복문에서 순회하는데 사용하는 변수 또는 인덱스  
- 값을 축적시키는데 사용하는 변수  
  
그 밖에 경우에 재할당 되는 변수가 있면 해당 변수는 여러 용도로 사용되는 것이며 변수를 분리해야 더 이해하기 좋은 코드를 만들 수 있다.  
- 변수 하나 당 하나의 책임을 지도록 만든다.  
- 상수를 활용하자. js의 const, 자바의 final  

```
1. final 키워드를 활용해 값의 고정을 명확하게 표현한다.
-- old

double acc = primaryForce / mass;
result = 0.5 * acc * primaryTime * primaryTime;

if (secondaryTime > 0) {
	double primaryVelocity = acc * delay;
	acc = (primaryForce + secondaryForce) / mass;
	result += primaryVelocity * secondaryTime + 0.5 * acc * secondaryTime + secondaryTime;
	...
}

-- new 

final double primaryAcceleration = primaryForce / mass;
result = 0.5 * primaryAcceleration * primaryTime * primaryTime;

if (secondaryTime > 0) {
	final double primaryVelocity = primaryAcceleration * delay;
	final double secondaryAcceleration = (primaryForce + secondaryForce) / mass;
	result += primaryVelocity * secondaryTime + 0.5 * secondaryAcceleration * secondaryTime + secondaryTime;
}
	...


2. input 파라미터를 그대로 사용하는것보다 변수를 사용하여 명확하게 표현한다.
-- old 

public double discount(double inputValue, int quantity) {
        if (inputValue > 50) inputValue = inputValue - 2;
        if (quantity > 100) inputValue = inputValue - 1;
        return inputValue;
}
    
-- new

public double discount(double inputValue, int quantity) {
        double result = inputValue;
        if (inputValue > 50) result -= 2;
        if (quantity > 100) result -= 1;
        return result;
}
```


#### 2. 질의 함수와 변경 함수 분리하기
"눈에 띌만한" 사이드 이팩트 없이 값을 조회할 수 있는 메소드는 테스트 하기도 쉽고, 메소드를 이동하기도 편하다.  
  
명령-조회 분리 규칙:  
- 어떤 값을 리턴하는 함수는 사이드 이팩트가 없어야 한다.  


```
1. 명확한 함수의 사용을 위해 조회와 알람 함수를 분리한다.
-- old
public String alertForMiscreant(List<Person> people) {
        for (Person p : people) {
            if (p.getName().equals("Don")) {
                setOffAlarms();
                return "Don";
            }

            if (p.getName().equals("John")) {
                setOffAlarms();
                return "John";
            }
        }
        return "";
}
-- new 
public void alertForMiscreant(List<Person> people) {
        if(!findMiscreant(people).isBlank()) setOffAlarms();
}

public String findMiscreant(List<Person> people) {
        for (Person p : people) {
            if (p.getName().equals("Don")) return "Don";
            if (p.getName().equals("John")) return "John";
        }

        return "";
}
```


#### 3. 세터 제거하기
세터를 제공한다는 것은 해당 필드가 변경될 수 있다는 것을 뜻한다.  
객체 생성시 처음 설정된 값이 변경될 필요가 없다면 해당 값을 설정할 수 있는 생성자를 만들고 세터를 제거해서 변경될 수 있는 가능성을 제거해야 한다.  


#### 4. 파생 변수를 질의 함수로 바꾸기  
변경할 수 있는 데이터를 최대한 줄이도록 노력해야 한다.  
계산해서 알아낼 수 있는 변수는 제거할 수 있다.  
- 계산 자체가 데이터의 의미를 잘 표현하는 경우도 있다.  
- 해당 변수가 어디선가 잘못된 값으로 수정될 수 있는 가능성을 제거할 수 있다.  
계산에 필요한 데이터가 변하지 않는 값이라면, 계산의 결과에 해당하는 데이터 역시 불변 데이터기 때문에 해당 변수는 그대로 유지할 수 있다.  
  
```
1. 계산해서 알아낼 수 있는 변수는 제거한다. 
-- old
public Discount(double baseTotal) {
        this.baseTotal = baseTotal;
}

public double getDiscountedTotal() {
        return this.discountedTotal;
}

public void setDiscount(double number) {
        this.discount = number;
        this.discountedTotal = this.baseTotal - this.discount;
}
    
-- new
public double getDiscountedTotal() {
        return calculatedDiscountedTotal();
}

private double calculatedDiscountedTotal() {
        return this.baseTotal - this.discount;
}


2. 1번과 마찬가지로 계산해서 알아낼 수 있는 production 변수를 제거한다.
-- old
public void applyAdjustment(double adjustment) {
        this.adjustments.add(adjustment);
        this.production += adjustment;
}

public double getProduction() {
        return this.production;
}

-- new
public void applyAdjustment(double adjustment) {
        this.adjustments.add(adjustment);
}

public double getProduction() {
        return this.adjustments.stream().reduce((double) 0, Double::sum);
}
```

#### 5. 참조를 값으로 바꾸기
값 객체는 객체가 가진 필드의 값으로 동일성을 확인한다.  
값 객체는 변하지 않는다.  
어떤 객체의 변경 내역을 다른 곳으로 전파시키고 싶다면 레퍼런스, 아니라면 값 객체를 사용한다.  
이러한 경우 값이 같더라도 다른 객체로 판단할 수 있기 때문에 equals, hascode를 재정의 해야한다.

</details>

<details markdown="7">
<summary> 7. 뒤엉킨 변경 </summary>    
소프트웨어는 변경에 유연하게(soft) 대처할 수 있어야 한다.  
    
어떤 한 모듈이(함수 또는 클래스가) 여러가지 이유로 다양하게 변경되어야 하는 상황.  
예) 새로운 결제 방식을 도입하거나, DB를 변경할 때 동일한 클래스에 여러 메소드를 수정해야 하는 경우.  
  
서로 다른 문제는 서로 다른 모듈에서 해결해야 한다.  
- 모듈의 책임이 분리되어 있을수록 해당 문맥을 더 잘 이해할 수 있으며 다른 문제는 신경쓰지 않아도 된다.  
  
좋은 소프트웨어는 응집도는 높아야하고 결합도는 낮아야 한다.  
응집도 : 얼마나 관련있는 것들이 한 곳에 잘 밀집되어 있는가.  
결합도 : 관련없는 것들이 서로 얼마나 의존하고 있는가.  
  
#### 1. 단계 쪼개기

서로 다른 일을 하는 코드를 각기 다른 모듈로 분리한다.  
- 그래야 어떤 것을 변경해야 할 때, 그것과 관련있는 것만 신경쓸 수 있다.  
  
여러 일을 하는 함수의 처리 과정을 각기 다른 단계로 구분할 수 있다.  
예) 전처리 -> 주요 작업 -> 후처리  
예) 컴파일러: 텍스트 읽어오기 -> 실행 가능한 형태로 변경  
  
서로 다른 데이터를 사용한다면 단계를 나누는데 있어 중요한 단서가 될 수 있다.  
중간 데이터를 만들어 단계를 구분하고 매개변수를 줄이는데 활요할 수 있다.  


```
-- old
public double priceOrder(Product product, int quantity, ShippingMethod shippingMethod) {
	final double basePrice = product.basePrice() * quantity;
        final double discount = Math.max(quantity - product.discountThreshold(), 0)
                * product.basePrice() * product.discountRate();
        final double shippingPerCase = (basePrice > shippingMethod.discountThreshold()) ?
                shippingMethod.discountedFee() : shippingMethod.feePerCase();
        final double shippingCost = quantity * shippingPerCase;
        final double price = basePrice - discount + shippingCost;
        return price;
}

-- new
// 하는 일에 따라 각기 다른 함수로 분리  

public double priceOrder(Product product, int quantity, ShippingMethod shippingMethod) {
        final PriceData priceData = calculatePriceData(product, quantity);
        return applyShipping(priceData, shippingMethod);
}

private PriceData calculatePriceData(Product product, int quantity) {
        final double basePrice = product.basePrice() * quantity;
        final double discount = Math.max(quantity - product.discountThreshold(), 0)
                * product.basePrice() * product.discountRate();

        return new PriceData(basePrice, discount, quantity); // 중간 데이터를 만들어 매개변수를 줄인다.
}

private double applyShipping(PriceData priceData, ShippingMethod shippingMethod) {
        final double shippingPerCase = (priceData.basePrice() > shippingMethod.discountThreshold()) ?
                shippingMethod.discountedFee() : shippingMethod.feePerCase();
        final double shippingCost = priceData.quantity() * shippingPerCase;
        return priceData.basePrice() - priceData.discount() + shippingCost;
}


```

#### 2. 함수 옮기기
모듈화가 잘 된 소프트웨어는 최소한의 지식만으로 프로그램을 변경할 수 있다.  
관련있는 함수나 필드가 모여있어야 더 쉽게 찾고 이해할 수 있다.  
하지만 관련있는 함수나 필드가 항상 고정적인 것은 아니기 때문에 때에 따라 옮겨야 할 필요가 있다.
  
함수를 옮겨야 하는 경우  
- 해당 함수가 다른 문맥(클래스)에 있는 데이터(필드)를 더 많이 참조하는 경우.  
- 해당 함수를 다른 클라이언트(클래스)에서도 필요로 하는 경우.  
  


#### 3. 클래스 추출하기  
클래스가 다루는 책임이 많아질수록 클래스가 점차 커진다.  
  
클래스를 쪼개는 기준  
- 데이터나 메소드 중 일부가 매우 밀접한 관련이 있는 경우.  
- 일부 데이터가 대부분 같이 바뀌는 경우.  
- 데이터 또는 메소드 중 일부를 삭제한다면 어떻게 될 것인가?    


하위 클래스를 만들어 책임을 분산 시킬 수도 있다.  

```
-- old  

private String officeAreaCode;
private String officeNumber;

public String telephoneNumber() {
	return this.officeAreaCode + " " + this.officeNumber;
}

-- new
// 하위 클래스로 위임.

private final TelephoneNumber telephoneNumber;
public String telephoneNumber() {
        return this.telephoneNumber.toString();
}

```
</details>


<details markdown="8">
<summary> 8. 산탄총 수술 </summary>    
  
어떤 한 변경 사항이 생겼을 때 여러 모듈을 수정해야 하는 상황. ("한가지" 변경사항으로 "여러 모듈" 수정)  
변경 사항이 여러곳에 흩어진다면 찾아서 고치기도 어렵고 중요한 변경 사항을 놓칠 수 있는 가능성도 생긴다.  
  
#### 1. 필드 옮기기

좋은 데이터 구조를 가지고 있다면, 해당 데이터에 기반한 어떤 행위를 코드로(메소드나 함수)옮기는 것도 간편하고 단순해진다.  
처음에는 타당해 보였던 설게적인 의사 결정도 프로그램이 다루고 있는 도메인과 데이터 구조에 대해 더 많이 익혀나가면서, 틀린 의사 결정으로 바뀌는 경우도 있다.  
  
필드를 옮기는 단서:  
- 어떤 데이터를 항상 어떤 클래스와 함께 전달하는 경우.  
- 어떤 클래스를 변경할 때 다른 클래스에 있는 필드를 변경해야 하는 경우.  
- 여러 클래스에 동일한 필드를 수정해야 하는 경우.  
  
```
// discountRate 필드를 CustomerContract 클래스로 옮겨 변경 사항이 발생했을때 대처하기 수월하게 리팩토링.

-- Customer Class
private CustomerContract contract;

public Customer(String name, double discountRate) {
	this.name = name;
        this.contract = new CustomerContract(dateToday(), discountRate);
}

public double getDiscountRate() {
        return this.contract.getDiscountRate();
}

```

#### 2. 함수 인라인
"함수 추출하기"의 반대에 해당하는 리팩토링  
간혹, 함수 본문(소스)이 함수 이름 만큼 또는 그보다 더 잘 의도를 표현하는 경우도 있다.  
  
```
-- old
//  소스를 읽지 않아도 moreThanFiveLateDeliveries 이라는 이름으로 잘 표현했지만, 소스와 메서드 이름의 큰 차이가 없다.

public int rating(Driver driver) {
        return moreThanFiveLateDeliveries(driver) ? 2 : 1;
}

private boolean moreThanFiveLateDeliveries(Driver driver) {
        return driver.getNumberOfLateDeliveries() > 5;
}

-- new 
public int rating(Driver driver) {
        return driver.getNumberOfLateDeliveries() > 5 ? 2 : 1;
}

```

#### 3. 클래스 인라인  
"클래스 추출하기"의 반대에 해당하는 리팩토링  
리팩토링을 하는 중에 클래스의 책임을 옮기다 보면 클래스의 존재 이유가 빈약해지는 경우가 발생할 수 있다.  
존재 이유가 빈약한 클래스의 필드먼저 옮기고 메서드를 옮긴 후 클래스를 삭제하면 컴파일 에러를 줄이며 편하게 옮길 수 있다.


</details>



<details markdown="9">
<summary> 9. 기능 편애 </summary>    
  
어떤 모듈에 있는 함수가 다른 모듈에 있는 데이터나 함수를 더 많이 참조하는 경우에 발생한다.  
예) 다른 객체의 getter를 여러개 사용하는 메소드  
- "함수 옮기기"를 사용해서 함수를 적절한 위치로 옮긴다.  
- 함수 일부분만 다른 곳의 데이터와 함수를 많이 참조한다면 "함수 추출하기"로 함수를 나눈 다음에 함수를 옮길 수 있다.  
  
만약에 여러 모듈을 참조하고 있다면, 그 중에서 가장 많은 데이터를 참조하는 곳으로 옮기거나, 함수를 여러개로 쪼개서 각 모듈로 분산 시킬 수 있다.  
데이터와 해당 데이터를 참조하는 행동을 같은 곳에 두도록 하자.  
  
  
```
-- old
// Bill Class에서 ElectricityUsage, GasUsage 클래스의 데이터를 참조하여 계산한다. 이것을 각각의 모듈로 옮겨 리팩토링한다.
double electicityBill = electricityUsage.getAmount() * electricityUsage.getPricePerUnit();
double gasBill = gasUsage.getAmount() * gasUsage.getPricePerUnit();
return electicityBill + gasBill;

-- new 

// ElectricityUsage Class
public double getElecticityBill() {
        return this.getAmount() * this.getPricePerUnit();
}

// GasUsage Class
public double getGasBill() {
        return this.getAmount() * this.getPricePerUnit();
}

// Bill Class
public double calculateBill() {
        return electricityUsage.getElecticityBill() + gasUsage.getGasBill();
}

```


</details>


<details markdown="9">
<summary> 10. 데이터 뭉치 </summary>    
  
항상 뭉쳐 다니는 데이터는 한 곳으로 모아두는 것이 좋다.  
- 여러 클래스에 존재하는 비슷한 필드 목록  
- 여러 함수에 전달하는 매개변수 목록    

```
-- old
// areaCode, number를 서로 다른 클래스에서 병합하여 phoneNumber로 사용하고 있다.


// Office Class
private String officeAreCode;
private String officeNumber;
public String officePhoneNumber() {
        return officeAreCode + "-" + officeNumber;
}


// Employee Class
private String personalAreaCode;
private String personalNumber;
public String personalPhoneNumber() {
        return personalAreaCode + "-" + personalNumber;
}


-- new
// 항상 뭉쳐다니며 같은 기능을 하는 필드를 새로운 클래스(TelephoneNumber Class)로 추출한다.

// TelephoneNumber Class
private String areaCode;
private String number;

@Override
public String toString() {
        return this.areaCode + "-" + this.number;
}

// Employee, Office Class
private TelephoneNumber personalPhoneNumber;
public String personalPhoneNumber() {
        return this.personalPhoneNumber.toString();
}
```

</details>



<details markdown="11">
<summary> 11. 기본형 집착 </summary>    

어플리케이션이 다루고 있는 도메인에 필요한 기본 타입을 만들지 않고 프로그래밍 언어가 제공하는 기본 타입을 사용하는 경우가 많다.  
예) 전화번호, 좌표, 돈, 범위, 수량 등  
기본형으로는 단위(인치 vs 미터) 또는 표기법을 표현하기 어렵다.  

#### 1. 기본형을 객체로 바꾸기  
개발 초기에는 기본형 (숫자 또는 문자열)으로 표현한 데이터가 나중에는 해당 데이터와 관련있는 다양한 기능을 필요로 하는 경우가 발생한다.  
예) 문자열로 표현하던 전화번호의 지역코드가 필요하거나 다양한 포맷을 지원하는 경우.  
예) 숫자로 표현하던 온도의 단위(화씨, 섭씨)를 변환하는 경우.  
기본형을 사용한 데이터를 감싸 줄 클래스를 만들면, 필요한 기능을 추가할 수 있다.  
  

```
-- old
...
.filter(o -> o.getPriority() == "high" || o.getPriority() == "rush")
...

-- new
// 기본형을 객체로 만들어 필요한 기능을 추가한다.
...
.filter(o -> o.getPriority().higherThen(new Priority("normal")))
...

// Priority Class
private List<String> legalValues = List.of("low", "normal", "high", "rush");
public boolean higherThen(Priority other) {
        return this.index() > other.index();
}

// Order Class 생성자의 String 매개변수의 type 체크가 필요하여 생성자 체인 사용.
public Order(String priority) {
        this(new Priority(priority));
}

public Order(Priority priority) {
        this.priority = priority;
}

// String 타입의 type safety를 검사
public Priority(String value) {
if (legalValues.contains(value)){
	this.value = value;
	...
	
```


#### 2. 타입 코드를 서브 클래스로 바꾸기  
비슷하지만 다른 것들을 표현해야 하는 경우, 문자열(String), 열거형(enum), 숫자(int) 등으로 표현하기도 한다.  
예) 주문타입, "일반 주문", "빠른 주문"  
예) 직원 타입, "엔지니어", "매니저", "세일즈"  
  
타입을 서브클래스로 바꾸는 계기  
- 조건문을 다형성으로 표현할 수 있을 때, 서브클래스로 만들고 "조건부 로직을 다형성으로 바꾸기"를 적용한다.  

```
-- old

private String type;
public Employee(String name, String type) {
        this.validate(type);
        this.name = name;
        this.type = type;
}
    
private void validate(String type) {
	List<String> legalTypes = List.of("engineer", "manager", "salesman");
	if (!legalTypes.contains(type)) throw new IllegalArgumentException(type);
	...

-- new 

// type 변수를 삭제하고 타입에 따른 서브클래스를 생성해 다형성으로 바꾼다.
public static Employee createEmployee(String name, String type) {
        switch (type) {
            case "engineer" :
                return new Engineer(name);
            case "manager" :
                return new Manager(name);
	    ...
            default:
                throw new IllegalArgumentException(type);
        }
}

assertEquals("engineer", Employee.createEmployee("hong", "engineer").getType());

```

#### 3. 조건부 로직을 다형성으로 바꾸기  
- 복잡한 조건식을 상속과 다형성을 사용해 코드를 보다 명확하게 분리할 수 있다.  
- 기본 동작과 (타입에 따른) 특수한 기능이 섞여있는 경우에 상속 구조를 만들어서 기본 동작을 상위클래스에 두고 특수한 기능을 하위 클래스로 옮겨서 각 타입에 따른 "차이점"을 강조할 수 있다.  
  

```
-- old
// 기존 코드에 type이 "china" 인지를 판별하여 다른 result 계산식이 존재한다.
private int captainHistoryRisk() {
        int result = 1;
        if (this.history.size() < 5) result += 4;
        result += this.history.stream().filter(v -> v.profit() < 0).count();
        if (this.voyage.zone().equals("china") && this.hasChinaHistory()) result -= 2; // HERE!!
        return Math.max(result, 0);
}

private int voyageProfitFactor() {
        int result = 2;

        if (this.voyage.zone().equals("china")) result += 1;
        if (this.voyage.zone().equals("east-indies")) result +=1 ;
        if (this.voyage.zone().equals("china") && this.hasChinaHistory()) { // HERE!!
            result += 3;
            if (this.history.size() > 10) result += 1;
            if (this.voyage.length() > 12) result += 1;
            if (this.voyage.length() > 18) result -= 1;
        } else {
            if (this.history.size() > 8) result +=1 ;
            if (this.voyage.length() > 14) result -= 1;
        }

        return result;
}

-- new 
// Factory 패턴을 적용해 type이 "china" 일 때 생성되는 객체를 다르게 만든다.
// VoyageRating 이라는 기본 동작 클래스를 상위에 두고 ChinaExperiencedVoyageRating 이라는 하위 클래스가 상속받아 메소드 오버라이딩을 통해 차이점을 구현한다.

// 다른 객체 생성
if(voyage.zone().equals("china") && hasChinaHistory(history)) {
            return new ChinaExperiencedVoyageRating(voyage, history);
        } else {
            return new VoyageRating(voyage, history);
}


// VoyageRating을 상속받은 ChinaExperiencedVoyageRating 하위 Class
protected int voyageLengthFactor() {
        int result = 0;
        result += 3;
        if (this.voyage.length() > 12) result += 1;
        if (this.voyage.length() > 18) result -= 1;
        return result;
}
```



</details>




<details markdown="12">
<summary> 12. 반복문 </summary>    

프로그래밍 언어 초기부터 있었던 반복문은 처음엔 별다른 대안이 없어서 간과했지만 최근 Java와 같은 언어에서 함수형 프로그래밍을 지원하면서 반복문에 비해 더 나은 대안책이 생겼다.  
"반복문을 파이프라인으로 바꾸는" 리팩토링을 적용하면 필터나 맵핑과 같은 파이프라인 기능을 사용해 보다 빠르게 어떤 작업을 하는지 파악할 수 있다.  
  
#### 1. 반복문을 파이프라인으로 바꾸기

컬렉션 파이프라인 (Java의 Stream)
고전적인 반복문을 파이프라인 오퍼레이션을 사용해 표현하면 코드를 더 명확하게 만들 수 있다.  
- 필터 : 전달받은 조건의 true에 해당하는 데이터만 다음 오퍼레이션으로 전달.
- 맵 : 전달받은 함수를 사용해 입력값을 원하는 출력값으로 변환하여 다음 오퍼레이션으로 전달.

```
-- old
for (Author a : authors) {
	if (a.company.equals(company)) {
                var handle = a.twitterHandle;
		if (handle != null) result.add(handle);
	}
}

-- new
// stream을 사용하여 리팩토링
authors.stream()
	.filter(author -> author.company.equals(company))
	.map(author -> author.twitterHandle)
	.filter(t -> t != null)
	.collect(Collectors.toList());
```

</details>



<details markdown="13">
<summary> 13. 성의없는 요소 </summary>    
 
여러 프로그래밍적인 요소(변수, 메소드, 클래스 등)를 만드는 이유  
- 나중에 발생할 변화를 대비해서  
- 해당 함수 또는 클래스를 재사용하려고  
- 의미있는 이름을 지어주려고  
  
가끔 그렇게 예상하고 만들어 놓은 요소들이 기대에 부응하지 못하는 경우가 있는데 그런 경우에 해당 요소들을 제거해야한다.  

#### 1. 계층 합치기
상속 구조를 리팩토링하는 중에 기능을 올리고 내리다 보면 하위클래스와 상위클래스 코드에 차이가 없는 경우가 발생할 수 있다.  
그런 경우에 그 둘을 합칠 수 있다.  
하위클래스와 상위클래스 중에 어떤 것을 없애야 하는가? (둘 중에 보다 이름이 적절한 쪽을 선택하지만, 애매하다면 어느쪽을 선택해도 문제없다.)


</details>

<details markdown="14">
<summary> 14. 추측성 일반화 </summary>    

나중에 이러 저러한 기능이 생길 것으로 예상하여, 여러 경우에 필요로 할만한 기능을 만들어 놨지만 결국에 쓰이지 않는 코드가 발생한 경우.  

#### 1. 죽은 코드제거하기
실제로 나중에 필요해질 코드라 하더라도 지금 쓰이지 않는 코드라면 주석으로 감싸는게 아니라 삭제해야 한다.

</details>



<details markdown="15">
<summary> 15. 임시필드 </summary>    
  
클래스에 있는 어떤 필드가 특정한 경우에만 값을 갖는 경우.  
어떤 객체의 필드가 "특정한 경우에만" 값을 가진다는 것을 이해하는 것은 일반적으로 예상하지 못하기 때문에 이해하기 어렵다.  
  
#### 1. 특이 케이스 추가하기
어떤 필드의 특정한 값에 따라 동일하게 동작하는 코드가 반복적으로 나타난다면, 해당 필드를 감싸는 "특별한 케이스"를 만들어 해당 조건을 표현할 수 있다.  
이러한 매커니즘을 "특이 케이스 패턴"이라고 부르고 "Null Object 패턴"을 이러한 패턴의 특수한 형태라고 볼 수 있다.  

```
1.
-- old
// 여러 메소드에서 customer의 name이 "unknown"인지 파악하는 코드가 반복된다. 
// 따라서 UnknownCustomer 이라는 클래스를 만들어 반복 코드를 리팩토링한다.

// CustomerService Class
public String customerName(Site site) {
	if (customer.getName().equals("unknown")) { // HERE!!
            customerName = "occupant";
        } else {
            customerName = customer.getName();
	}
	...
	
public BillingPlan billingPlan(Site site) {
	return customer.getName().equals("unknown") ? new BasicBillingPlan() : customer.getBillingPlan(); // HERE!!
...

public int weeksDelinquent(Site site) {
	return customer.getName().equals("unknown") ? 0 : customer.getPaymentHistory().getWeeksDelinquentInLastYear(); // HERE!!
...

-- new
// UnknownCustomer Class extends Customer
public UnknownCustomer() {
        super("unknown", null, null);
}

public String customerName(Site site) {
        return site.getCustomer().getName();
}	


2.
-- old
// CustomerService에서 사용되는 Customer 정보는 site.getCustomer() 메서드를 통해 가져온다.
// 이 메서드에서 Customer의 타입을 판별해 가지고 온다면 메서드를 간결하게 리팩토링할 수 있다.


// Site Class
public Site(Customer customer) {
        this.customer = customer.getName().equals("unknown") ? new UnknownCustomer() : customer;
}

// Customer Service
public BillingPlan billingPlan(Site site) {
        return site.getCustomer().getBillingPlan();
}

// UnkownCustomer Class
public UnknownCustomer() {
        super("unknown", new BasicBillingPlan(), null);
}



3.
-- old
// CustomerServiced의 weeksDelinquent메서드의 경우 UnknownCustomer의 경우 Null Object 패턴을 사용해 디폴트 0 값을 설정해준다.

public int weeksDelinquent(Site site) {
        Customer customer = site.getCustomer();
        return customer.getName().equals("unknown") ? 0 : customer.getPaymentHistory().getWeeksDelinquentInLastYear();
}

-- new
// UnkownCustomer Class
public UnknownCustomer() {
        super("unknown", new BasicBillingPlan(), new NullPaymentHistory());
}

// NullPaymentHistory Class
public NullPaymentHistory() {
        super(0);
}

// CustomerService Class
public int weeksDelinquent(Site site) {
        return site.getCustomer().getPaymentHistory().getWeeksDelinquentInLastYear();
}

```

</details>



<details markdown="16">
<summary> 16. 메시지 체인 </summary>    
  
레퍼런스를 따라 계속해서 메소드 호출이 이어지는 코드  
예) this.member.getCredit().getLevel().getDescription()  
해당 코드의 클라이언트가 코드 체인을 모두 이해해야 한다.  
체인 중 일부가 변경된다면 클라이언트의 코드도 변경해야 한다.  
  


#### 1. 위임 숨기기
  
캡슐화란 어떤 모듈이 시스템의 다른 모듈을 최소한으로 알아야 한다는 것이다.  
그래야 어떤 모듈을 변경할 때, 최소한의 모듈만 그 변경에 영향을 받을 것이고, 그래야 무언가를 변경하기 쉽다.  
처음 객체 지향에서 캡슐화를 배울 때 필드를 메소드로 숨기는 것이라 배우지만, 메소드 호출도 숨길 수 있다.

```
-- old
Person manager = keesun.getDepartment().getManager();

-- new

// Person Class
Person getManager(Person keesun) {
        return keesun.getDepartment().getManager();
}

Person manager = keesun.getManager(keesun);
```




</details>


<details markdown="16">
<summary> 17. 중재자 </summary>    
캡슐화를 통해 내부의 구체적인 정보를 최대한 감출 수 있다.  
그러나, 어떤 클래스의 메소드가 대부분 다른 캘래스로 메소드 호출을 위임하고 있다면 중재자를 제거하고 클라이언트가 해당 클래스를 직접 사용하도록 코드를 개선할 수 있다.  


#### 1. 중재자 제거하기
16번 위임 숨기기의 반대.  
필요한 캡슐화의 정도는 시간에 따라 그리고 상황에 따라 바뀔 수 있다.  
위임하고 있는 객체를 클라이언트가 사용할 수 있도록 getter를 제공하고, 클라이언트는 메시지 체인을 사용하도록 코드를 고친 뒤에 캡슐화에 사용했던 메소드를 제거한다.  



</details>
