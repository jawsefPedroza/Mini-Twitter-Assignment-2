// MiniTwitter.java
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

// Main class representing the Mini Twitter application
public class MiniTwitter {
    private UserGroup rootGroup;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    public MiniTwitter() {
        this.rootGroup = new UserGroup("Root");
        this.rootNode = new DefaultMutableTreeNode(rootGroup);
        this.treeModel = new DefaultTreeModel(rootNode);
    }//end of deafult constructor 
    //Adds User to the app
    public void addUser(User user, UserGroup group) {
        group.addUser(user);
        DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(user);
        DefaultMutableTreeNode parentNode = findNode(rootNode, group);
        if (parentNode != null) {
            parentNode.add(userNode);
            treeModel.reload(parentNode); // Update the parent node in the tree model
        }
    }//end addUser
    //add group to app
    public void addGroup(UserGroup group, UserGroup parentGroup) {
        parentGroup.addGroup(group);
        DefaultMutableTreeNode parentNode = findNode(rootNode, parentGroup);
        if (parentNode != null) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group, true);
            parentNode.add(groupNode);
            treeModel.reload(parentNode); // Update the parent node in the tree model
        }
    }//end addGroup
    
    public User findUser(String userId) {
        return findUser(userId, rootGroup);
    }//end findUser
    
    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode parentNode, UserGroup group) {
        // Recursively search for the tree node representing the given group
        if (parentNode.getUserObject().equals(group)) {
            return parentNode;
        } else {
            int childCount = parentNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                DefaultMutableTreeNode result = findNode(childNode, group);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }//end findNode
//Admin contol panel and its functions
    public void displayAdminControlPanel() {
        JFrame frame = new JFrame("Admin Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create UI components
        JButton createUserButton = new JButton("Create User");
        JTextField userIdTextField = new JTextField(15);
        JButton createGroupButton = new JButton("Create Group");
        JTextField groupIdTextField = new JTextField(15);
        JButton showTotalUsersButton = new JButton("Total Users");
        JButton showTotalGroupsButton = new JButton("Total Groups");
        JButton showTotalTweetsButton = new JButton("Total Tweets");
        JButton showPositiveTweetsButton = new JButton("Positive Tweets");
        JButton openUserViewButton = new JButton("Open User View");
        
        JTree userTree = new JTree(treeModel);
        userTree.setCellRenderer(new FolderTreeCellRenderer());
        JScrollPane treeScrollPane = new JScrollPane(userTree);

        // Configure layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Create User: "), constraints);
        constraints.gridx++;
        panel.add(userIdTextField, constraints);
        constraints.gridx++;
        panel.add(createUserButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(new JLabel("Create Group: "), constraints);
        constraints.gridx++;
        panel.add(groupIdTextField, constraints);
        constraints.gridx++;
        panel.add(createGroupButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(showTotalUsersButton, constraints);

        constraints.gridx++;
        panel.add(showTotalGroupsButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(showTotalTweetsButton, constraints);

        constraints.gridx++;
        panel.add(showPositiveTweetsButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(treeScrollPane, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(openUserViewButton, constraints);
        //all of the bottons
        // Add event listeners
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdTextField.getText().trim();
                if (!userId.isEmpty()) {
                    // Check if the user already exists
                    if (findUser(userId) != null) {
                        JOptionPane.showMessageDialog(frame, "User already exists!");
                    } else if (userId.contains(" ")) {
                        JOptionPane.showMessageDialog(frame, "User ID cannot contain spaces!");
                    } else {
                        User newUser = new User(userId);
                        addUser(newUser, rootGroup);
                        userIdTextField.setText("");
                    }
                }
            }
        });
        // Make a group
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId = groupIdTextField.getText().trim();
                if (!groupId.isEmpty()) {
                    // Check if the group already exists
                    if (findUser(groupId) != null || findGroup(groupId) != null) {
                        JOptionPane.showMessageDialog(frame, "Group already exists!");
                    } else if (groupId.contains(" ")) {
                        JOptionPane.showMessageDialog(frame, "Group ID cannot contain spaces!");
                    } else {
                        UserGroup newGroup = new UserGroup(groupId);
                        addGroup(newGroup, rootGroup);
                        groupIdTextField.setText("");
                    }
                }
            }
        });

        showTotalUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalUsers = countTotalUsers(rootGroup);
                JOptionPane.showMessageDialog(frame, "Total Users: " + totalUsers);
            }
        });

        showTotalGroupsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalGroups = countTotalGroups(rootGroup);
                JOptionPane.showMessageDialog(frame, "Total Groups: " + totalGroups);
            }
        });

        showTotalTweetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalTweets = countTotalTweets(rootGroup);
                JOptionPane.showMessageDialog(frame, "Total Tweets: " + totalTweets);
            }
        });

        showPositiveTweetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalTweets = countTotalTweets(rootGroup);
                int positiveTweets = countPositiveTweets(rootGroup);
                double percentage = (double) positiveTweets / totalTweets * 100;
                JOptionPane.showMessageDialog(frame, "Positive Tweets: " + String.format("%.2f", percentage) + "%");
            }
        });

        openUserViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) userTree.getLastSelectedPathComponent();
                if (selectedNode != null && selectedNode.getUserObject() instanceof User) {
                    User selectedUser = (User) selectedNode.getUserObject();
                    displayUserView(selectedUser);
                }
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

private int countTotalUsers(UserGroup group) {
        int count = group.getUsers().size();
        for (UserGroup subGroup : group.getSubGroups()) {
            count += countTotalUsers(subGroup);
        }
        return count;
    }//end countTotalUser

 private int countTotalGroups(UserGroup group) {
    int count = group.getSubGroups().size();
    for (UserGroup subGroup : group.getSubGroups()) {
        count += countTotalGroups(subGroup);
    }
    return count;
}//end countTotalGroups

private int countTotalTweets(UserGroup group) {
    int count = 0;
    for (User user : group.getUsers()) {
        count += user.getTweetCount();
    }
    for (UserGroup subGroup : group.getSubGroups()) {
        count += countTotalTweets(subGroup);
    }
    return count;
}//end countTotalTweets

private int countPositiveTweets(UserGroup group) {
    int count = 0;
    for (User user : group.getUsers()) {
        for (String tweet : user.getNewsFeed()) {
            if (isPositiveTweet(tweet)) {
                count++;
            }
        }
    }
    for (UserGroup subGroup : group.getSubGroups()) {
        count += countPositiveTweets(subGroup);
    }
    return count;
}//end countPositiveTweets

private boolean isPositiveTweet(String tweet) {
    // Customize this method to define what constitutes a positive tweet
    String[] positiveWords = {"good", "great", "excellent"};
    tweet = tweet.toLowerCase(); // Convert tweet to lowercase for case-insensitive comparison
    for (String word : positiveWords) {
        if (tweet.contains(word)) {
            return true;
        }
    }
    return false;
}//end isPositiveTweet

    private UserGroup findGroup(String groupId, UserGroup group) {
        if (group.getId().equals(groupId)) {
            return group;
        } else {
            for (UserGroup subGroup : group.getSubGroups()) {
                UserGroup foundGroup = findGroup(groupId, subGroup);
                if (foundGroup != null) {
                    return foundGroup;
                }
            }
            return null;
        }
    }//end findGroup
    //end od admin^
    private UserGroup findGroup(String groupId) {
        return findGroup(groupId, rootGroup);
    }
   //all of the users pannel bottons and display
    public void displayUserView(User user) {
        JFrame frame = new JFrame(user.getId() + " User View");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        // Create UI components
        DefaultListModel<String> followingListModel = new DefaultListModel<>();
        JList<String> followingList = new JList<>(followingListModel);
        JScrollPane followingScrollPane = new JScrollPane(followingList);
    
        DefaultListModel<String> newsFeedListModel = new DefaultListModel<>();
        JList<String> newsFeedList = new JList<>(newsFeedListModel);
        JScrollPane newsFeedScrollPane = new JScrollPane(newsFeedList);
    
        JTextField followUserIdTextField = new JTextField(15);
        JButton followUserButton = new JButton("Follow");
    
        JTextField tweetTextField = new JTextField(15);
        JButton postTweetButton = new JButton("Post Tweet");
    
        // Configure layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
    
        panel.add(new JLabel("Following:"), constraints);
    
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 4;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(followingScrollPane, constraints);
    
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Follow User:"), constraints);
    
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(followUserIdTextField, constraints);
    
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(followUserButton, constraints);
    
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(new JLabel("News Feed:"), constraints);
    
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridheight = 2;
        panel.add(newsFeedScrollPane, constraints);
    
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Tweet:"), constraints);
    
        constraints.gridx = 3;
        constraints.gridy = 1;
        panel.add(tweetTextField, constraints);
    
        constraints.gridx = 3;
        constraints.gridy = 2;
        panel.add(postTweetButton, constraints);
    
        // Add event listeners
       followUserButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String userId = followUserIdTextField.getText().trim();
        if (!userId.isEmpty()) {
            User userToFollow = findUser(userId); // Assuming findUser is defined properly
            if (userToFollow != null && !userToFollow.equals(user)) {
                user.addFollowing(userToFollow);
                followingListModel.addElement("- " + userToFollow.getId()); // Add "- " before the user ID
                followUserIdTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "User not found!");
            }
        }
    }
});
      
        postTweetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the tweet from the text field
                String tweet = tweetTextField.getText().trim();
                if (!tweet.isEmpty()) {
                    user.postTweet(" - " + user.getId() + " : " + tweet); // Add a dash before the user ID
                    tweetTextField.setText("");
                    updateNewsFeed(newsFeedListModel, user);
                }
            }
        });
            // Set up the initial view
       // Set up the initial view
       updateFollowingList(followingListModel, user);
       updateNewsFeed(newsFeedListModel, user);

// Add the panel to the frame and display the window
frame.getContentPane().add(panel);
frame.pack();
frame.setVisible(true);
}

// Helper method to update the following list
private void updateFollowingList(DefaultListModel<String> model, User user) {
model.clear();
for (User following : user.getFollowing()) {
model.addElement(following.getId());
}
}//end updateFollowingList

// Helper method to update the news feed
private void updateNewsFeed(DefaultListModel<String> model, User user) {
    model.clear();

    // Add tweets from user's news feed
    for (String tweet : user.getNewsFeed()) {
        model.addElement(tweet);
    }

    // Add tweets from other users' news feeds
    for (UserGroup group : rootGroup.getSubGroups()) {
        addTweetsFromGroup(model, group);
    }
}//end updateNewsFeed

private void addTweetsFromGroup(DefaultListModel<String> model, UserGroup group) {
    for (User user : group.getUsers()) {
        for (String tweet : user.getNewsFeed()) {
            model.addElement(tweet);
        }
    }

    for (UserGroup subGroup : group.getSubGroups()) {
        addTweetsFromGroup(model, subGroup);
    }
}//end addTweetsFromGroup

    private User findUser(String userId, UserGroup group) {
        for (User user : group.getUsers()) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        for (UserGroup subGroup : group.getSubGroups()) {
            User user = findUser(userId, subGroup);
            if (user != null) {
                return user;
            }
        }
        return null;
    }//end findUser
    //end of userdiplay
}//end of the MiniTwitter