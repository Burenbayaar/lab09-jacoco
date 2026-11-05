# Лаборатори №9: Код хамралт — JaCoCo

**F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)**

- **Оюутны нэр:** Э.Бүрэнбаяр
- **Оюутны код:** B242270084

## Орчин

~~~
$ java -version
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-24.04.4-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-1-24.04.4-Ubuntu, mixed mode, sharing)

$ mvn -version
[1mApache Maven 3.8.7[m
Maven home: /usr/share/maven
Java version: 21.0.12.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "7.0.0-42-generic", arch: "amd64", family: "unix"
~~~

## Төслийн бүтэц

~~~
lab08-mockito/
├── pom.xml
├── README.md
├── results/
│   ├── mvn-test.txt
│   ├── mvn-test-mutant-1.txt
│   ├── mvn-test-mutant-2.txt
│   ├── mvn-test-mutant-3-before-fix.txt
│   └── mvn-test-mutant-3.txt
└── src/
    ├── main/java/mn/edu/must/sqat/
    │   ├── Student.java
    │   ├── StudentRepository.java
    │   ├── EmailSender.java
    │   └── CourseRegistrationService.java
    └── test/java/mn/edu/must/sqat/
        └── CourseRegistrationServiceTest.java
~~~

`pom.xml`-д `maven-compiler-plugin` 3.13.0-г тодорхой зааж өгсөн: Ubuntu-ийн `apt`-ийн Maven анхдагчаар
compiler-plugin 3.1 ашигладаг бөгөөд тэр нь `maven.compiler.release=17`-г ойлгодоггүй тул
"Source option 5 is no longer supported" алдаа өгч байсан.

## Хэсэг A: Тестүүд

`mvn test` → **Tests run: 10, Failures: 0, Errors: 0, Skipped: 0**, BUILD SUCCESS
(нотолгоо: [`results/mvn-test.txt`](results/mvn-test.txt)). 7 энгийн тест + A8-ийн 3 мөр.

| №  | Тестийн нэр | Гол хэрэгсэл |
|----|-------------|--------------|
| A1 | `register_activeStudentWithPrerequisites_returnsOkSavesAndSendsEmail` | `when` + `verify` |
| A2 | `register_unknownStudent_returnsErrorNoStudentAndDoesNotSaveOrEmail` | `verify(..., never())` |
| A3 | `register_inactiveStudent_returnsErrorInactiveAndNeverTouchesEmail` | `verifyNoInteractions` |
| A4 | `register_missingPrerequisite_returnsErrorPrerequisitesAndDoesNotSave` | `when` + `never` |
| A5 | `register_emailSenderThrows_stillReturnsOkAndRegistrationIsSaved` | `doThrow(...).when(...)` |
| A6 | `register_nullStudentIdOrCourseId_throwsIllegalArgumentAndTouchesNoDependency` | `assertThrows` |
| A7 | `register_success_emailBodyContainsCourseId` | `ArgumentCaptor` |
| A8 | `register_invalidInput_returnsMatchingErrorAndDoesNotSaveOrEmail` (3 мөр) | `@ParameterizedTest` + `@CsvSource` |

A8-ийн гурван мөр: `ERROR_NO_STUDENT`, `ERROR_INACTIVE_STUDENT`, `ERROR_NO_COURSE`.
`ERROR_NO_COURSE`-г A1–A7-ийн аль нь ч шалгадаггүй тул A8-д оруулж энэ цоорхойг бөглөсөн.

Matcher-ийн дүрэм: нэг дуудалтад matcher хэрэглэсэн бол бүх аргументад matcher
(`eq(...)`, `anyString()`, `contains(...)`) хэрэглэсэн; matcher-гүй дуудалтад бүгд бодит утга.

### A8 ба `lenient()`

A8-ийн мөр бүр `register()`-ийн өөр алхам дээр зогсдог (жишээ нь `ERROR_NO_STUDENT` мөрөнд
`courseExists`, `prerequisites` огт дуудагдахгүй), гэтэл бүх мөрөнд ижил stub-уудыг тохируулдаг.
`MockitoExtension`-ий strict stubs горим ашиглагдаагүй stub-ыг `UnnecessaryStubbingException`
болгодог тул эдгээр stub-ыг `lenient().when(...)` хэлбэрээр зарлаж, "энэ мөрөнд ашиглагдахгүй
байж болно" гэдгийг Mockito-д тодорхой хэлсэн.

## Хэсэг B: Мини мутаци

| Мутант | Эвдрэл | Унасан тестүүд | Төрөл | Файл |
|--------|--------|----------------|-------|------|
| 1 | `if (!student.active()) ...` шалгалтыг комментолсон | A3 `register_inactiveStudent_...`, A8 `[2]` (`ERROR_INACTIVE_STUDENT` мөр) | Failures: 2 | [`mvn-test-mutant-1.txt`](results/mvn-test-mutant-1.txt) |
| 2 | `repository.saveRegistration(...)` дуудалтыг комментолсон | A1 `register_activeStudentWithPrerequisites_...`, A5 `register_emailSenderThrows_...` | Failures: 2 | [`mvn-test-mutant-2.txt`](results/mvn-test-mutant-2.txt) |
| 3 | Эхний `if`-ээс `\|\| courseId == null`-ийг устгасан | Анх: **нэг ч тест унаагүй**, BUILD SUCCESS ([before-fix](results/mvn-test-mutant-3-before-fix.txt)). A6-г зассаны дараа: A6 `register_nullStudentIdOrCourseId_...` | Failures: 1 | [`mvn-test-mutant-3.txt`](results/mvn-test-mutant-3.txt) |

**Мутант 1.** A3-т `courseExists` stub хийгээгүй тул mock `false` буцааж, үйлчилгээ
`ERROR_NO_COURSE` буцаана → `assertEquals` унана. A8-ийн 2-р мөрөнд хичээл байгаа, холбоо
хичээл хоосон тул бүртгэл хийгдэж `"OK"` буцаана → унана.

**Мутант 2.** `verify(repository).saveRegistration(...)` агуулсан A1, A5 унасан.
A7 зөвхөн и-мэйлийг шалгадаг тул энэ мутантад ногоон үлдсэн — нэг тест бүх зүйлийг
шалгах албагүй, харин тестийн багц бүхэлдээ барих ёстой.

**Мутант 3 ба засвар.** Анхны A6 зөвхөн `register(null, COURSE_ID)`-г шалгадаг байсан тул
`courseId == null` шалгалтыг устгахад нэг ч тест унаагүй — тестийн цоорхой. A6-д
`register(STUDENT_ID, null)`-д `IllegalArgumentException` шидэгдэхийг шалгах хоёр дахь
`assertThrows` нэмж, нэрийг `register_nullStudentIdOrCourseId_...` болгосон (тестийн тоо 10 хэвээр).
Засварын дараа мутант дээр `findStudent` stub-гүй тул `null` буцаж, үйлчилгээ exception шидэхийн оронд
`"ERROR_NO_STUDENT"` буцаана → A6 унана.

**Хэсэг B дүгнэлт.** Гурван мутантын хоёрыг анхны тестүүд шууд барьсан, гуравдахь нь
спецификацийн "хоёул шаардлагатай" гэсэн нөхцөлийн зөвхөн хагасыг шалгаж байсныг илрүүлсэн.
Бүх тест ногоон байх нь кодын бүх логик шалгагдсан гэсэн үг биш — зориуд эвдэж үзэх нь
тест үнэхээр ажил хийж байгааг батлах хамгийн энгийн арга юм. Мутаци бүрийн дараа код
сэргээгдэж, эцсийн `results/mvn-test.txt` дахин ногоон гарсан.

## Дүгнэлт

Mock нь тест дотор Mockito-гоор динамикаар үүсгэгддэг, "ямар дуудлагад юу буцаах" (stub) болон
"ямар дуудлага хийгдсэн бэ" (verify) гэдгийг тест бүрт тохируулж шалгах боломжтой объект юм;
гараар бичсэн fake нь интерфейсийг бодитоор (жишээ нь `HashMap` дээр суурилсан сан) хэрэгжүүлсэн
хөнгөн класс бөгөөд төлөв хадгалдаг, олон тестэд дахин ашиглагддаг ч харилцааг шалгах чадваргүй.
Mock нь гадаад, удаан эсвэл тодорхойгүй хамаарлуудад (өгөгдлийн сан, SMTP, HTTP API, цаг)
тохиромжтой. Харин `Student` record шиг энгийн утгын объект, тестлэгдэж буй класс өөрөө,
мөн өөрийн эзэмшдэггүй гуравдагч талын төрлийг шууд mock хийх нь буруу — ийм үед бодит объект
эсвэл өөрийн интерфейсээр боосон адаптер ашиглах нь зөв. `verify`-г хэтрүүлбэл тест үр дүнг
бус хэрэгжүүлэлтийн дотоод алхмуудыг (дуудлагын дараалал, тоо) шалгаж эхэлнэ. Ингэснээр
зан төлөв өөрчлөгдөөгүй рефакторинг ч тестийг унагаж, тест "хэврэг" болдог. Тиймээс энэ
лабораторид `verify`-г зөвхөн спецификацид шууд бичигдсэн гаж нөлөөнд (бүртгэл хадгалагдах,
и-мэйл явах/явахгүй) хэрэглэсэн.
