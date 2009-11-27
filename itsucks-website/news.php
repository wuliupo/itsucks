<?
$selected_menu_entry = 'news';

include_once('include/header.php.inc');
?>

    <div class="main">

      <p class="text">
        <h2>Version 0.4.0 is released</h2>
        <div class='date'>2009-11-26</div>
        <p>
		This release contains a complete rewrite of the resume logic. Resuming can be disabled over the GUI.<br>
		New is also the support for referrals. Many sites use them to ensure you are not using deeplinks. (They can be disabled over the GUI though.)<br>
		Also a issue is fixed with parameters in redirects (403). (Bug 2680409, thanks for the detailed bug report)
		</p>
		Other changes:
		<ul>
		<li>Switched to JAXB 2.0</li>
		<li>Updated Maven configuration (2.2)</li>
		<li>Refactoring HttpRetriever Interface</li>
		<li>Build source jars</li>
		</ul>		
	</p>
	<p>
		Detailed changelog can be found here: <a href="https://sourceforge.net/projects/itsucks/files/itsucks/itsucks-releasenotes-0.4.0.txt/view"> ChangeLog 0.4.0</a>
	</p>
      </p>

      <p class="text">
        <h2>Version 0.3.1 is released</h2>
        <div class='date'>2008-08-29</div>
        <p>
		Fixed VM Version check.
	</p>
	<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?release_id=622855"> ChangeLog 0.3.1</a>
	</p>
      </p>

 

      <p class="text">
        <h2>Version 0.3.0 is released</h2>
        <div class='date'>2008-08-01</div>
        <p>
		This release is finally a stable one. :-)<br>
		It includes major memory usage improvements both in GUI and Console mode.<br>
		Console mode now use less memory than the GUI mode.
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=617192"> ChangeLog 0.3.0</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0-pre7 is released</h2>
        <div class='date'>2008-05-25</div>
        <p>
		This release contains a large rebuild of the GUI. It also adds cookie and authentication support.
        <ul>
        <li>Added online help. (Press F1 to access)</li>
        <li>Job Configuration is now structured as a tree instead of tabs.</li>
        <li>Added http authentication support</li>
        <li>Added cookie support</li>
        <li>Added GZip compression support</li>
        </ul>
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=601938"> ChangeLog 0.3.0-pre7</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0-pre6 is released</h2>
        <div class='date'>2008-04-05</div>
        <p>
		This release contains a new bandwith limit and configurable HTTP Status Code behaviour.  
        <ul>
        <li>Added bandwith limit.</li>
        <li>Added configurable HTTP Status Code behaviour</li>
        <li>Minor bugfixes.</li>
        </ul>
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=582023"> ChangeLog 0.3.0-pre6</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0-pre5 is released</h2>
        <div class='date'>2008-01-12</div>
        <p>
		Fifth pre release of the 0.3.0 branch. It contains a new content filter.
        <ul>
        <li>Added content filter to match text files with repexp patterns while downloading them.</li>
        <li>Added text field to change user agent.</li>
        <li>Added cool application icon :-)</li>
        <li>Bugfixes solving problems with lockups (threading issues). </li>
        </ul>
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=568134"> ChangeLog 0.3.0-pre5</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0-pre4 is released</h2>
        <div class='date'>2007-12-30</div>
        <p>
		Forth pre release of the 0.3.0 branch. It contains batch processing and multiple url's per job.
        <ul>
        <li>Added batch processing</li>
        <li>Added multiple url's per job</li>
        <li>Improved memory settings for JVM</li>
        </ul>
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=564962"> ChangeLog 0.3.0-pre4</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0-pre3 is released</h2>
        <div class='date'>2007-11-04</div>
        <p>
		Third pre release of the 0.3.0 branch. It contains a new time limit filter.
        <ul>
        <li>Added time limit filter</li>
        <li>Added injection of context into job filter</li>
        </ul>
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=551850"> ChangeLog 0.3.0-pre3</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0-pre2 is released</h2>
        <div class='date'>2007-10-24</div>
        <p>
		Second pre release of the 0.3.0 branch. It contains a new file size filter.
        <ul>
        <li>Added new tab 'special filters' including the new file size filter.</li>
        <li>Added support to abort processing chains</li>
        </ul>
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=549312"> ChangeLog 0.3.0-pre2</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0-pre1 is released</h2>
        <div class='date'>2007-10-12</div>
        <p>
		The first pre release of the version 0.3.0 is released. It should
		be pretty stable so don't hesitate to download it.
        <ul>
        <li>Complete rewrite of the GUI, makes it much easier to use.</li>
        <li>Proxy support</li>
        <li>Configurable count of threads per server</li>
        <li>Refactoring of core api to make using easier and more intuitive</li> 
        </ul>
		</p>
		<p>
		I'm happy about feedback and please post any bug/issue you find in the forum.  
		</p>		
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=545989"> ChangeLog 0.3.0-pre1</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.3.0 in progress...</h2>
        <div class='date'>2007-09-10</div>
        <p>
        Currently I'm working at the next release of ItSucks. 
        It will contain many new features:
        <ul>
        <li>Complete rewrite of the GUI, makes it much easier to use.</li>
        <li>Proxy support</li>
        <li>Configurable count of threads per server</li>
        </ul>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0 final is released</h2>
        <div class='date'>2007-09-02</div>
        <p>
		This release is the stable version of 0.2.0.
		</p>
		<p>
		Please post any bug/issue you find in the forum.  
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=536682"> ChangeLog 0.2.0</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0-b4 is released</h2>
        <div class='date'>2007-08-14</div>
        <p>
		This release adds a Java Runtime Engine Version check.
		</p>
		<p>
		Please post any bug/issue you find in the forum.  
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=532231"> ChangeLog 0.2.0-b4</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0-b3 is released</h2>
        <div class='date'>2007-07-24</div>
        <p>
		This is majorly a bugfix release.  
		</p>
		<p>
		Please post any bug/issue you find in the forum.  
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=525866"> ChangeLog 0.2.0-b3</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0-b2 is released</h2>
        <div class='date'>2007-07-07</div>
        <p>
		This is majorly a bugfix release.  
		</p>
		<p>
		Please post any bug/issue you find in the forum.  
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?release_id=521512&group_id=186141"> ChangeLog 0.2.0-b2</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0-b1 is released</h2>
        <div class='date'>2007-06-17</div>
        <p>
		Most changes of this release are under the hood of ItSucks. It includes a complete redesign of the processing chain of incoming data. This makes it much easier to insert own data processor into the chain.
		Also a complete javadoc from the core api is available under <a href="http://itsucks.sf.net/apidocs">http://itsucks.sf.net/apidocs</a>
		</p>
		<p>
		The project is now in beta status, so please post any bug/issue you find in the forum.  
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=516383"> ChangeLog 0.2.0-b1</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0-pre9 is released</h2>
        <div class='date'>2007-05-24</div>
        <p>
		Most changes of this release are under the hood of ItSucks. It includes a new automatic
		retry when an retryable http error occurs and an centralized event message dispatcher.
		</p>
		<p>
		The project is slowly nearing beta status, so please post any bug/issue you find in the forum.
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?group_id=186141&release_id=511002"> ChangeLog 0.2.0-pre9</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0-pre8 is released</h2>
        <div class='date'>2007-04-10</div>
        <p>
		The most important new features are the pause feature and that advanced filter are 
		re-editable after creating them. Also very interesting is the new console interface. 
		Create your download using the GUI and save it as download template. 
		The download can be started later on the console with 'java -jar itsucks-console-0.2.0-pre8.jar &lt;download template&gt;'. 
		Many bugfixes are included as well.
		</p>
		<p>
		The project is slowly nearing beta status, so please post any bug/issue you find in the forum.
		</p>
		<p>
		Detailed changelog can be found here: <a href="http://sourceforge.net/project/shownotes.php?release_id=500059&group_id=186141"> ChangeLog 0.2.0-pre8</a>
		</p>
      </p>

      <p class="text">
        <h2>Version 0.2.0-pre7 is released</h2>
        <div class='date'>2007-03-31</div>
        Majorly an bugfix release. New is the pause function and an console only util.
      </p>
    
      <p class="text">
        <h2>ItSucks Website published</h2>
        <div class='date'>2007-03-27</div>
        Published the ItSucks website.
      </p>
    
      <p class="text">
        <h2>Version 0.2.0-pre6 is released</h2>
        <div class='date'>2007-03-11</div>
        This new test version includes two new features. Resuming of partially downloaded files and load/save of download configurations.
      </p>

      <p class="text">
        <h2>Version 0.2.0-pre5 is released</h2>
        <div class='date'>2007-02-18</div>
        Released a new test version of itsucks.<br/> 
        The most important change is the displaying of the progress of every download.<br/> 
        Some bugfixes are included as well. 
      </p>

      <p class="text">
        <h2>Version 0.2.0-pre4 is released</h2>
        <div class='date'>2007-02-01</div>
        Released a new test version of itsucks.<br/> 
        It contains bugfixes for the usage under windows and a large performance improvment in the core library.
      </p>      
    </div>
    
<?
include_once('include/footer.php.inc');
?>
