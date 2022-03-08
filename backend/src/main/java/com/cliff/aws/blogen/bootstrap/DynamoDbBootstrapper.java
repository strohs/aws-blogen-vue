package com.cliff.aws.blogen.bootstrap;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@Slf4j
@Profile({"dev"})
public class DynamoDbBootstrapper {

//    @Value("${blogen.recreate.table.if.exists:false}")
//    private boolean shouldRecreateTable;

    private final AmazonDynamoDB dynamoDB;
    private final DynamoDBMapper dbMapper;
    private final DynamoDBMapperConfig dbMapperConfig;
    private final UserPoolBootstrapper userPoolBootstrapper;

    private static final String IMG_SERVICE = "https://picsum.photos/300/200";
    private static final String IMG_SERVICE_BUSINESS = "https://picsum.photos/300/200/?image=1070";
    private static final String IMG_SERVICE_CITY = "https://picsum.photos/300/200/?image=1071";
    private static final String IMG_SERVICE_ABSTRACT = "https://picsum.photos/300/200/?image=1072";
    private static final String IMG_SERVICE_NATURE = "https://picsum.photos/300/200/?image=1073";
    private static final String IMG_SERVICE_FOOD = "https://picsum.photos/300/200/?image=1074";
    private static final String IMG_SERVICE_GREY = "https://picsum.photos/g//300/200/?image=1075";

    @Autowired
    public DynamoDbBootstrapper(
            AmazonDynamoDB dynamoDB,
            DynamoDBMapper dbMapper,
            DynamoDBMapperConfig dbMapperConfig,
            UserPoolBootstrapper userPoolBootstrapper) {
        this.dynamoDB = dynamoDB;
        this.dbMapper = dbMapper;
        this.dbMapperConfig = dbMapperConfig;
        this.userPoolBootstrapper = userPoolBootstrapper;
    }


    /**
     *
     */
    public void initData() {
        List<DynamoDBMapper.FailedBatch> failedBatches;

        // build categories keys
        log.debug("building categories");
        List<Blogen> categories = buildBlogenCategories();
        failedBatches = dbMapper.batchSave(categories);// CLOBBERS any existing items
        logFailedBatches(failedBatches);

        // build avatar file names keys
        log.debug("building avatars");
        List<Blogen> avatars = buildBlogenAvatarFileNames();
        failedBatches = dbMapper.batchSave(avatars);// CLOBBERS any existing items
        logFailedBatches(failedBatches);

        // Build threads and posts
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);

        String threadId1, threadId2, threadId3;
        Blogen thread, thread2, thread3, thread4, thread5, thread6, thread7, thread8, thread9, thread10, post1, post2, post3, post4, post5, post6;
        // retrieve the users from the userPoolBootstrapper
        Blogen william = userPoolBootstrapper.getUserByEmail("scotsman@example.com");
        Blogen maggie = userPoolBootstrapper.getUserByEmail("mcgill@example.com");
        Blogen john = userPoolBootstrapper.getUserByEmail("johndoe@example.com");
        Blogen elizabeth = userPoolBootstrapper.getUserByEmail("lizreed@example.com");
        Blogen admin = userPoolBootstrapper.getUserByEmail("admin@example.com");

        //build posts for william - 2 parent posts
        threadId1 = UUID.randomUUID().toString();
        threadId2 = UUID.randomUUID().toString();
        thread = buildBlogenPost(threadId1, threadId1, william.getUserId(), william.getUserName(), william.getAvatarFileName(),
                "Health & Fitness", "Started lifting today", "Trying to burn off these holiday calories. I hear resistance training is better than running",
                sequentialImageUrl(), yesterday.plus(6, ChronoUnit.MINUTES));
        post1 = buildBlogenPost(threadId2, threadId2, william.getUserId(), william.getUserName(), william.getAvatarFileName(),
                "Business", "Bulls are on parade", "They stock markets won't stop running higher. When will the bubble burst?",
                sequentialImageUrl(), yesterday.plus(7, ChronoUnit.MINUTES));
        failedBatches = dbMapper.batchSave(Arrays.asList(thread, post1));
        logFailedBatches(failedBatches);

        //build posts for maggie - 3 parent posts with 2 child posts each
        threadId1 = UUID.randomUUID().toString();
        thread = buildBlogenPost(threadId1, threadId1, maggie.getUserId(), maggie.getUserName(), maggie.getAvatarFileName(),
                "Business", "Bitcoin or bust", "Forget about gold, I'm all in on bitcoin",
                sequentialImageUrl(), yesterday.plus(8, ChronoUnit.MINUTES));
        post1 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), john.getUserId(), john.getUserName(), john.getAvatarFileName(),
                "Business", "probably buying it", "I'm game too. I just don't know where to but it from",
                sequentialImageUrl(), yesterday.plus(9, ChronoUnit.MINUTES));
        post2 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), maggie.getUserId(), maggie.getUserName(), maggie.getAvatarFileName(),
                "Business", "beware the bubble", "If we've waited this long, I fear it's already too late",
                sequentialImageUrl(), yesterday.plus(10, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread2 = buildBlogenPost(threadId1, threadId1, maggie.getUserId(), maggie.getUserName(), maggie.getAvatarFileName(),
                "Health & Fitness", "What ever happened to Ty Bo?", "It used to be all the rage, now I can't find a single gym that offers it",
                sequentialImageUrl(), yesterday.plus(11, ChronoUnit.MINUTES));
        post3 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), william.getUserId(), william.getUserName(), william.getAvatarFileName(),
                "Health & Fitness", "sounds cool", "What do you do, fight each other until you pass out?",
                sequentialImageUrl(), yesterday.plus(12, ChronoUnit.MINUTES));
        post4 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), william.getUserId(), william.getUserName(), william.getAvatarFileName(),
                "Health & Fitness", "on second thought...", "I changed my mind. It looks kinda fun. Someone get my leg warmers",
                sequentialImageUrl(), yesterday.plus(13, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread3 = buildBlogenPost(threadId1, threadId1, maggie.getUserId(), maggie.getUserName(), maggie.getAvatarFileName(),
                "Web Design", "Is PHP dead?", "Does anyone have stats on PHP usage in the wild?",
                sequentialImageUrl(), yesterday.plus(14, ChronoUnit.MINUTES));
        post5 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), william.getUserId(), william.getUserName(), william.getAvatarFileName(),
                "Web Design", "I doubt it", "PHP is everywhere. I'm pretty sure it still powers the internet!",
                sequentialImageUrl(), yesterday.plus(15, ChronoUnit.MINUTES));
        post6 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), admin.getUserId(), admin.getUserName(), admin.getAvatarFileName(),
                "Web Design", "We don't use it here", "...anymore. We switched to Kotlin/React, but a lot of companies are still powered by PHP",
                sequentialImageUrl(), yesterday.plus(16, ChronoUnit.MINUTES));

        failedBatches = dbMapper.batchSave(Arrays.asList(thread, post1, post2, thread2, post3, post4, thread3, post5, post6));
        logFailedBatches(failedBatches);


        //Build Posts for elizabeth (10 parent posts for her)
        threadId1 = UUID.randomUUID().toString();
        thread = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Business", "Invest now", "Market returns are crazy, there is still time to jump on in",
                sequentialImageUrl(), yesterday.plus(17, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread2 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Health & Fitness", "Proper Diet trumps all", "No matter what excercise you do, just remember you can never out-train a poor diet",
                sequentialImageUrl(), yesterday.plus(18, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread3 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Technology", "About Alexa", "Does anyone own one of these? Is it any good?",
                sequentialImageUrl(), yesterday.plus(19, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread4 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Web Development", "Bootstrap 4", "Hey you all. Would it be worth my time to learn Bootstrap 4?",
                sequentialImageUrl(), yesterday.plus(20, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread5 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Business", "Buying gold", "I wanna buy some gold. Can someone point me in the right direction",
                sequentialImageUrl(), yesterday.plus(21, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread6 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Health & Fitness", "HIIT Training", "Forget about running for hours on end. High Intensity Interval Training can give you all the benefits in half the time",
                sequentialImageUrl(), yesterday.plus(22, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread7 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Technology", "Toys that teach Programming", "My nephew is showing an interest in programming. Can anyone recommend something for a ten year old?",
                sequentialImageUrl(), yesterday.plus(23, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread8 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Web Development", "Clojure Script", "You guys need to try this http://clojure.org, It saved me hours of web dev work",
                sequentialImageUrl(), yesterday.plus(24, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread9 = buildBlogenPost(threadId1, threadId1, elizabeth.getUserId(), elizabeth.getUserName(), elizabeth.getAvatarFileName(),
                "Technology", "Samsung Galaxy 8", "This phone is the greatest. Nice screen, good battery life, and tons of apps!",
                sequentialImageUrl(), yesterday.plus(25, ChronoUnit.MINUTES));

        threadId1 = UUID.randomUUID().toString();
        thread10 = buildBlogenPost(threadId1, threadId1, admin.getUserId(), admin.getUserName(), admin.getAvatarFileName(),
                "Web Development", "Welcome to Web Design", "Please post any and all web design questions under this category",
                sequentialImageUrl(), yesterday.plus(26, ChronoUnit.MINUTES));

        failedBatches = dbMapper.batchSave(Arrays.asList(thread, thread2, thread3, thread4, thread5, thread6, thread7, thread8, thread9, thread10));
        logFailedBatches(failedBatches);

        //  build posts for John - 1 thread with 4 child posts
        threadId1 = UUID.randomUUID().toString();
        thread = buildBlogenPost(threadId1, threadId1, john.getUserId(), john.getUserName(), john.getAvatarFileName(),
                "Technology", "Love this tech", "Smart-phones are the greatest invention in the history of mankind",
                sequentialImageUrl(), yesterday.plus(27, ChronoUnit.MINUTES));
        post1 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), john.getUserId(), john.getUserName(), john.getAvatarFileName(),
                "Technology", "Love it too", "I wish I could embed the phone into my head",
                sequentialImageUrl(), yesterday.plus(28, ChronoUnit.MINUTES));
        post2 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), maggie.getUserId(), maggie.getUserName(), maggie.getAvatarFileName(),
                "Technology", "Not so fast", "Are they even greater than the Internet?",
                sequentialImageUrl(), yesterday.plus(29, ChronoUnit.MINUTES));
        post3 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), william.getUserId(), william.getUserName(), william.getAvatarFileName(),
                "Technology", "Here today gone tomorrow", "They're the greatest for yesterday, but something better will come along",
                sequentialImageUrl(), yesterday.plus(30, ChronoUnit.MINUTES));
        post4 = buildBlogenPost(threadId1, UUID.randomUUID().toString(), admin.getUserId(), admin.getUserName(), admin.getAvatarFileName(),
                "Technology", "No No No", "the greatest invention is velcro :)",
                sequentialImageUrl(), yesterday.plus(31, ChronoUnit.MINUTES));
        failedBatches = dbMapper.batchSave(Arrays.asList(thread, post1, post2, post3, post4));
        logFailedBatches(failedBatches);


        //there should now be 26 posts in total - sixteen thread start posts and ten child posts
    }

    /**
     * creates the blogen dynamodb table and Global Secondary Indices
     */
    private void buildBlogenTable() {
        CreateTableRequest ctr = BootstrapUtils.createBlogenTableRequest(dbMapper, dbMapperConfig, 1L, 1L);
        BootstrapUtils.checkOrCreateTable(dynamoDB, ctr, true, true);
    }

    // this maps a userName to the userId that is currently assigned to that userName
    // userName is used as the hash key, while the userId is used as the range key
    private List<Blogen> buildUserNames(Blogen... users) {
        List<Blogen> userNames = new ArrayList<>();
        Arrays.stream(users).forEach(user -> {
            userNames.add(buildUserName(user.getUserName(), user.getPrimaryHash()));
        });
        return userNames;
    }

    private List<Blogen> buildBlogenCategories() {
        List<Blogen> categories = new ArrayList<>();
        categories.add(buildCategory("Technology"));
        categories.add(buildCategory("Health & Fitness"));
        categories.add(buildCategory("Web Design"));
        categories.add(buildCategory("Business"));
        categories.add(buildCategory("Java Programming"));
        return categories;
    }

    private List<Blogen> buildBlogenAvatarFileNames() {
        List<Blogen> avatars = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            avatars.add(buildAvatarFileName("avatar" + i + ".jpg"));
        }
        return avatars;
    }

    public Blogen buildBlogenPost(String threadId, String postId, String userId,
                                  String userName, String avatarFileName, String categoryName,
                                  String title, String text, String imageUrl, Instant created) {

        String rangeKey = Blogen.buildPostRangeKey(created, postId);
        BlogenPrimaryKey pk = BlogenPrimaryKey.builder().primaryHash(threadId).primaryRange(rangeKey).build();
        return Blogen.builder()
                .blogenPrimaryKey(pk)
                .threadId(threadId)
                .postId(postId)
                .userId(userId)
                .userName(userName)
                .avatarFileName(avatarFileName)
                .categoryName(categoryName)
                .title(title)
                .text(text)
                .imageUrl(imageUrl)
                .updatedTimestamp(created)
                .build();
    }

//    public Blogen buildBlogenUser( String userId, String userName, String firstName, String lastName,
//                                   String email, String password, Instant created, String avatarFileName,
//                                   boolean enabled, String ...roles) {
//        return Blogen.builder()
//                .blogenPrimaryKey( buildUserPK( userId ) )
//                .userName( userName )
//                .firstName( firstName )
//                .lastName( lastName )
//                .email( email )
//                .password( password )
//                .avatarFileName( avatarFileName )
//                .updatedTimestamp( created )
//                .enabled( enabled )
//                .roles( new HashSet<>( Arrays.asList(roles) ))
//                .build();
//    }

    public static Blogen buildCategory(String name) {
        return Blogen.builder()
                .blogenPrimaryKey(buildCategoryPK(name))
                .categoryName(name)
                .updatedTimestamp(Instant.now())
                .build();
    }

    public static Blogen buildAvatarFileName(String fileName) {
        return Blogen.builder()
                .blogenPrimaryKey(buildAvatarFileNamePK(fileName))
                .updatedTimestamp(Instant.now())
                .build();
    }

    public static Blogen buildUserName(String userName, String userId) {
        return Blogen.builder()
                .blogenPrimaryKey(buildUserNamePK(userName, userId))
                .updatedTimestamp(Instant.now())
                .build();
    }

    public static BlogenPrimaryKey buildCategoryPK(String hash) {
        return BlogenPrimaryKey.builder().primaryHash(hash).primaryRange(Blogen.RANGE_CATEGORY).build();
    }

    public static BlogenPrimaryKey buildAvatarFileNamePK(String hash) {
        return BlogenPrimaryKey.builder().primaryHash(hash).primaryRange(Blogen.RANGE_AVATAR).build();
    }

    // the userName Primary Key has a userId of the user assigned to the userName as the range key
    public static BlogenPrimaryKey buildUserNamePK(String userName, String userId) {
        return BlogenPrimaryKey.builder().primaryHash(userName).primaryRange(userId).build();
    }

    public static BlogenPrimaryKey buildUserPK(String userId) {
        return BlogenPrimaryKey.builder().primaryHash(userId).primaryRange(Blogen.RANGE_USER).build();
    }

    private void logFailedBatches(List<DynamoDBMapper.FailedBatch> failedBatches) {
        failedBatches.forEach(fb -> {
            fb.getUnprocessedItems().forEach((s, writeRequests) -> log.debug("FAILED TO SAVE: {} with exception: {}", s, fb.getException().getMessage()));
        });
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private static int imageStart = 1058;

    ///quick and dirty random image url
    private static String sequentialImageUrl() {
        return IMG_SERVICE + "/?image=" + imageStart++;
    }


    /**
     * deletes the dynamoDB table created by this class
     */
    protected void deleteResources() {
        BootstrapUtils.deleteTable(this.dynamoDB, "Blogen", true);
    }
    /**
     * starts the bootstrapping process.
     * - Creates one dynamoDB table named "Blogen"
     * - creates 5 categories in the table
     * - creates 7 avatar file names in the table
     * - retrieves the 5 users created by the userPoolBootstrapper and uses their IDs to build
     * 26 posts in total
     */
    public void bootstrap() {

        buildBlogenTable();

        log.info("bootstrapping sample data");
        initData();
        log.info("Finished bootstrapping data");

    }
}
