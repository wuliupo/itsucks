<?
$selected_menu_entry = 'about';

include_once('include/header.php.inc');
?>

    <div class="main">
      <p class="text">
      
        <h2>Requirements to run ItSucks</h2>
      	<ul>
          <li>Java JRE 1.5 or better (1.6 recommended). 
          You can get it at <a href="http://java.sun.com">http://java.sun.com</a>.</li>
          <li>min. 256 MB RAM (the more the better, java craves for memory)</li>
        </ul>
      
        <h2>Features of ItSucks Core library</h2>
        
        <h3>General Features</h3>
        <ul>
          <li>Designed to supported multiple protocols. At this time only http is implemented.</li>
          <li>Running multiple crawl jobs at one time.</li>
          <li>Count of working threads for every crawl job is changeable. (Not over the GUI available yet.)</li>
          <li>Search depth is configurable.</li>
          <li>Support for individual priority of an URL.</li>
          <li>Save and load one or multiple Jobs. (serialize / deserialize)</li>
          <li>Fully programmed with Java 1.5 features (generics etc.).</li>
        </ul>
        
        <h3>URL Filter Features</h3>
        <ul>
          <li>Hostname is configurable by regular expressions.
          <li>Type of files to be downloaded is configurable by regular expressions.</li>
          <li>A base URL can be set to follow only URL's beginning with this prefix.</li>
          <li>A highly configurable advanced filter can hold multiple regular expressions. 
          For every expression, actions can be defined when the regular expression matches the URL
          and actions to be executed when the expression does not match.<br/>
          Possible actions are: 
          follow the URL (Accept), do not follow the URL (Reject), change the priority of the URL</li>
        </ul>
  
        <h2>Features of ItSucks Swing GUI</h2>
        <ul>
          <li>Keep track of multiple downloads and one time.</li>
          <li>Add and remove download jobs.</li>
          <li>A integrated regular expression tester to test/debug your regular expression directly.</li>
          <li>Load and save downloads as templates.</li>
        </ul>
        
        <h2>External libraries/tools used</h2>
        <ul>
          <li><a href="http://www.springframework.org/">Spring Application Framework</a></li>
          <li><a href="http://maven.apache.org/">Maven2</a></li>
          <li><a href="http://jakarta.apache.org/commons/httpclient/">Jakarta Commons HttpClient</a></li>
          <li><a href="http://logging.apache.org/log4j/">log4j</a> 
          + <a href="http://jakarta.apache.org/commons/logging/">Jakarta Commons Logging</a></li>
        </ul>
        
      </p>    
    
<?
include_once('include/footer.php.inc');
?>