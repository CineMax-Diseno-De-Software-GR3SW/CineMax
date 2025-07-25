#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Start Up Batch script
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# -----------------
#   M2_HOME - location of maven2's installed home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
#       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
#   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ] ; then

  if [ -f /usr/local/etc/mavenrc ] ; then
    . /usr/local/etc/mavenrc
  fi

  if [ -f /etc/mavenrc ] ; then
    . /etc/mavenrc
  fi

  if [ -f "$HOME/.mavenrc" ] ; then
    . "$HOME/.mavenrc"
  fi

fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
mingw=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  MINGW*) mingw=true;;
  Darwin*) darwin=true
    # Use /usr/libexec/java_home if available, otherwise fall back to /Library/Java/Home
    # See https://developer.apple.com/library/mac/qa/qa1170/_index.html
    if [ -z "$JAVA_HOME" ]; then
      if [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="`/usr/libexec/java_home`"
      else
        export JAVA_HOME="/Library/Java/Home"
      fi
    fi
    ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if [ -r /etc/gentoo-release ] ; then
    JAVA_HOME=`java-config --jre-home`
  fi
fi

if [ -z "$M2_HOME" ] ; then
  ## resolve links - $0 may be a link to maven's home
  PRG="$0"

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="`dirname "$PRG"`/$link"
    fi
  done

  saveddir=`pwd`

  M2_HOME=`dirname "$PRG"`/..

  # make it fully qualified
  M2_HOME=`cd "$M2_HOME" && pwd`

  cd "$saveddir"
  # echo Using m2 at $M2_HOME
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$M2_HOME" ] &&
    M2_HOME=`cygpath --unix "$M2_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For Mingw, ensure paths are in UNIX format before anything is touched
if $mingw ; then
  [ -n "$M2_HOME" ] &&
    M2_HOME="`(cd "$M2_HOME"; pwd)`"
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME="`(cd "$JAVA_HOME"; pwd)`"
fi

if [ -z "$JAVA_HOME" ]; then
  javaExecutable="`which javac`"
  if [ -n "$javaExecutable" ] && ! [ "`expr \"$javaExecutable\" : '\([^ ]*\)'`" = "no" ]; then
    # readlink(1) is not available as standard on Solaris 10.
    readLink=`which readlink`
    if [ ! `expr "$readLink" : '\([^ ]*\)'` = "no" ]; then
      if $darwin ; then
        javaHome="`dirname \"$javaExecutable\"`"
        javaExecutable="`cd \"$javaHome\" && pwd -P`/javac"
      else
        javaExecutable="`readlink -f \"$javaExecutable\"`"
      fi
      javaHome="`dirname \"$javaExecutable\"`"
      javaHome=`expr "$javaHome" : '\(.*\)/bin'`
      JAVA_HOME="$javaHome"
      export JAVA_HOME
    fi
  fi
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD="`\\unset -f command; \\command -v java`"
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  We cannot execute $JAVACMD" >&2
  exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
  echo "Warning: JAVA_HOME environment variable is not set."
fi

CLASSWORLDS_LAUNCHER=org.codehaus.plexus.classworlds.launcher.Launcher

# traverses directory structure from process work directory to filesystem root
# first directory with .mvn subdirectory is considered project base directory
find_maven_basedir() {

  if [ -z "$1" ]
  then
    echo "Path not specified to find_maven_basedir"
    return 1
  fi

  basedir="$1"
  wdir="$1"
  while [ "$wdir" != '/' ] ; do
    if [ -d "$wdir"/.mvn ] ; then
      basedir=$wdir
      break
    fi
    # workaround for JBEAP-8937 (on Solaris 10/Sparc)
    if [ -d "${wdir}" ]; then
      wdir=`cd "$wdir/.."; pwd`
    fi
    # end of workaround
  done
  echo "${basedir}"
}

# concatenates all lines of a file
concat_lines() {
  if [ -f "$1" ]; then
    echo "$(tr -s '\n' ' ' < "$1")"
  fi
}

BASE_DIR=`find_maven_basedir "$(pwd)"`
if [ -z "$BASE_DIR" ]; then
  exit 1;
fi

##########################################################################################
# Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
# This allows using the maven wrapper in projects that prohibit checking in binary data.
##########################################################################################
if [ -r "$BASE_DIR/.mvn/wrapper/maven-wrapper.jar" ]; then
    if [ "$MVNW_VERBOSE" = true ]; then
      echo "Found .mvn/wrapper/maven-wrapper.jar"
    fi
else
    if [ "$MVNW_VERBOSE" = true ]; then
      echo "Couldn't find .mvn/wrapper/maven-wrapper.jar, downloading it ..."
    fi
    if [ -n "$MVNW_REPOURL" ]; then
      jarUrl="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
    else
      jarUrl="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
    fi
    while IFS="=" read key value; do
      case "$key" in (wrapperUrl) jarUrl="$value"; break ;;
      esac
    done < "$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
    if [ "$MVNW_VERBOSE" = true ]; then
      echo "Downloading from: $jarUrl"
    fi
    wrapperJarPath="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
    if $cygwin; then
      wrapperJarPath=`cygpath --path --windows "$wrapperJarPath"`
    fi

    if command -v wget > /dev/null; then
        if [ "$MVNW_VERBOSE" = true ]; then
          echo "Found wget ... using wget"
        fi
        if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ]; then
            wget "$jarUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
        else
            wget --http-user=$MVNW_USERNAME --http-password=$MVNW_PASSWORD "$jarUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
        fi
    elif command -v curl > /dev/null; then
        if [ "$MVNW_VERBOSE" = true ]; then
          echo "Found curl ... using curl"
        fi
        if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ]; then
            curl -o "$wrapperJarPath" "$jarUrl" -f
        else
            curl --user $MVNW_USERNAME:$MVNW_PASSWORD -o "$wrapperJarPath" "$jarUrl" -f
        fi

    else
        if [ "$MVNW_VERBOSE" = true ]; then
          echo "Falling back to using Java to download"
        fi
        javaClass="$BASE_DIR/.mvn/wrapper/MavenWrapperDownloader.java"
        # For Cygwin, switch paths to Windows format before running javac
        if $cygwin; then
          javaClass=`cygpath --path --windows "$javaClass"`
        fi
        if [ -e "$javaClass" ]; then
            if [ ! -e "$BASE_DIR/.mvn/wrapper/MavenWrapperDownloader.class" ]; then
                if [ "$MVNW_VERBOSE" = true ]; then
                  echo " - Compiling MavenWrapperDownloader.java ..."
                fi
                # Compiling the Java class
                ("$JAVA_HOME/bin/javac" "$javaClass")
            fi
            if [ -e "$BASE_DIR/.mvn/wrapper/MavenWrapperDownloader.class" ]; then
                # Running the downloader
                if [ "$MVNW_VERBOSE" = true ]; then
                  echo " - Running MavenWrapperDownloader.java ..."
                fi
                ("$JAVA_HOME/bin/java" -cp .mvn/wrapper MavenWrapperDownloader "$MAVEN_PROJECTBASEDIR")
            fi
        fi
    fi
fi
##########################################################################################
# End of extension
##########################################################################################

export MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$BASE_DIR"}
if [ "$MVNW_VERBOSE" = true ]; then
  echo $MAVEN_PROJECTBASEDIR
fi
MAVEN_OPTS="$(concat_lines "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config") $MAVEN_OPTS"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$M2_HOME" ] &&
    M2_HOME=`cygpath --path --windows "$M2_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$MAVEN_PROJECTBASEDIR" ] &&
    MAVEN_PROJECTBASEDIR=`cygpath --path --windows "$MAVEN_PROJECTBASEDIR"`
fi

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $@"
export MAVEN_CMD_LINE_ARGS

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" \
  "-Dmaven.home=${M2_HOME}" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  ${WRAPPER_LAUNCHER} $MAVEN_CONFIG "$@"
#DE ELLOSSSSSS
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when download maven via java source
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; debug: trace the mvnw script; others: silence the output
# ----------------------------------------------------------------------------
#DESDE AQUI CTRL K + C
# set -euf
# [ "${MVNW_VERBOSE-}" != debug ] || set -x

# # OS specific support.
# native_path() { printf %s\\n "$1"; }
# case "$(uname)" in
# CYGWIN* | MINGW*)
#   [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
#   native_path() { cygpath --path --windows "$1"; }
#   ;;
# esac

# # set JAVACMD and JAVACCMD
# set_java_home() {
#   # For Cygwin and MinGW, ensure paths are in Unix format before anything is touched
#   if [ -n "${JAVA_HOME-}" ]; then
#     if [ -x "$JAVA_HOME/jre/sh/java" ]; then
#       # IBM's JDK on AIX uses strange locations for the executables
#       JAVACMD="$JAVA_HOME/jre/sh/java"
#       JAVACCMD="$JAVA_HOME/jre/sh/javac"
#     else
#       JAVACMD="$JAVA_HOME/bin/java"
#       JAVACCMD="$JAVA_HOME/bin/javac"

#       if [ ! -x "$JAVACMD" ] || [ ! -x "$JAVACCMD" ]; then
#         echo "The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run." >&2
#         echo "JAVA_HOME is set to \"$JAVA_HOME\", but \"\$JAVA_HOME/bin/java\" or \"\$JAVA_HOME/bin/javac\" does not exist." >&2
#         return 1
#       fi
#     fi
#   else
#     JAVACMD="$(
#       'set' +e
#       'unset' -f command 2>/dev/null
#       'command' -v java
#     )" || :
#     JAVACCMD="$(
#       'set' +e
#       'unset' -f command 2>/dev/null
#       'command' -v javac
#     )" || :

#     if [ ! -x "${JAVACMD-}" ] || [ ! -x "${JAVACCMD-}" ]; then
#       echo "The java/javac command does not exist in PATH nor is JAVA_HOME set, so mvnw cannot run." >&2
#       return 1
#     fi
#   fi
# }

# # hash string like Java String::hashCode
# hash_string() {
#   str="${1:-}" h=0
#   while [ -n "$str" ]; do
#     char="${str%"${str#?}"}"
#     h=$(((h * 31 + $(LC_CTYPE=C printf %d "'$char")) % 4294967296))
#     str="${str#?}"
#   done
#   printf %x\\n $h
# }

# verbose() { :; }
# [ "${MVNW_VERBOSE-}" != true ] || verbose() { printf %s\\n "${1-}"; }

# die() {
#   printf %s\\n "$1" >&2
#   exit 1
# }

# trim() {
#   # MWRAPPER-139:
#   #   Trims trailing and leading whitespace, carriage returns, tabs, and linefeeds.
#   #   Needed for removing poorly interpreted newline sequences when running in more
#   #   exotic environments such as mingw bash on Windows.
#   printf "%s" "${1}" | tr -d '[:space:]'
# }

# # parse distributionUrl and optional distributionSha256Sum, requires .mvn/wrapper/maven-wrapper.properties
# while IFS="=" read -r key value; do
#   case "${key-}" in
#   distributionUrl) distributionUrl=$(trim "${value-}") ;;
#   distributionSha256Sum) distributionSha256Sum=$(trim "${value-}") ;;
#   esac
# done <"${0%/*}/.mvn/wrapper/maven-wrapper.properties"
# [ -n "${distributionUrl-}" ] || die "cannot read distributionUrl property in ${0%/*}/.mvn/wrapper/maven-wrapper.properties"

# case "${distributionUrl##*/}" in
# maven-mvnd-*bin.*)
#   MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/
#   case "${PROCESSOR_ARCHITECTURE-}${PROCESSOR_ARCHITEW6432-}:$(uname -a)" in
#   *AMD64:CYGWIN* | *AMD64:MINGW*) distributionPlatform=windows-amd64 ;;
#   :Darwin*x86_64) distributionPlatform=darwin-amd64 ;;
#   :Darwin*arm64) distributionPlatform=darwin-aarch64 ;;
#   :Linux*x86_64*) distributionPlatform=linux-amd64 ;;
#   *)
#     echo "Cannot detect native platform for mvnd on $(uname)-$(uname -m), use pure java version" >&2
#     distributionPlatform=linux-amd64
#     ;;
#   esac
#   distributionUrl="${distributionUrl%-bin.*}-$distributionPlatform.zip"
#   ;;
# maven-mvnd-*) MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/ ;;
# *) MVN_CMD="mvn${0##*/mvnw}" _MVNW_REPO_PATTERN=/org/apache/maven/ ;;
# esac

# # apply MVNW_REPOURL and calculate MAVEN_HOME
# # maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
# [ -z "${MVNW_REPOURL-}" ] || distributionUrl="$MVNW_REPOURL$_MVNW_REPO_PATTERN${distributionUrl#*"$_MVNW_REPO_PATTERN"}"
# distributionUrlName="${distributionUrl##*/}"
# distributionUrlNameMain="${distributionUrlName%.*}"
# distributionUrlNameMain="${distributionUrlNameMain%-bin}"
# MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
# MAVEN_HOME="${MAVEN_USER_HOME}/wrapper/dists/${distributionUrlNameMain-}/$(hash_string "$distributionUrl")"

# exec_maven() {
#   unset MVNW_VERBOSE MVNW_USERNAME MVNW_PASSWORD MVNW_REPOURL || :
#   exec "$MAVEN_HOME/bin/$MVN_CMD" "$@" || die "cannot exec $MAVEN_HOME/bin/$MVN_CMD"
# }

# if [ -d "$MAVEN_HOME" ]; then
#   verbose "found existing MAVEN_HOME at $MAVEN_HOME"
#   exec_maven "$@"
# fi

# case "${distributionUrl-}" in
# *?-bin.zip | *?maven-mvnd-?*-?*.zip) ;;
# *) die "distributionUrl is not valid, must match *-bin.zip or maven-mvnd-*.zip, but found '${distributionUrl-}'" ;;
# esac

# # prepare tmp dir
# if TMP_DOWNLOAD_DIR="$(mktemp -d)" && [ -d "$TMP_DOWNLOAD_DIR" ]; then
#   clean() { rm -rf -- "$TMP_DOWNLOAD_DIR"; }
#   trap clean HUP INT TERM EXIT
# else
#   die "cannot create temp dir"
# fi

# mkdir -p -- "${MAVEN_HOME%/*}"

# # Download and Install Apache Maven
# verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
# verbose "Downloading from: $distributionUrl"
# verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

# # select .zip or .tar.gz
# if ! command -v unzip >/dev/null; then
#   distributionUrl="${distributionUrl%.zip}.tar.gz"
#   distributionUrlName="${distributionUrl##*/}"
# fi

# # verbose opt
# __MVNW_QUIET_WGET=--quiet __MVNW_QUIET_CURL=--silent __MVNW_QUIET_UNZIP=-q __MVNW_QUIET_TAR=''
# [ "${MVNW_VERBOSE-}" != true ] || __MVNW_QUIET_WGET='' __MVNW_QUIET_CURL='' __MVNW_QUIET_UNZIP='' __MVNW_QUIET_TAR=v

# # normalize http auth
# case "${MVNW_PASSWORD:+has-password}" in
# '') MVNW_USERNAME='' MVNW_PASSWORD='' ;;
# has-password) [ -n "${MVNW_USERNAME-}" ] || MVNW_USERNAME='' MVNW_PASSWORD='' ;;
# esac

# if [ -z "${MVNW_USERNAME-}" ] && command -v wget >/dev/null; then
#   verbose "Found wget ... using wget"
#   wget ${__MVNW_QUIET_WGET:+"$__MVNW_QUIET_WGET"} "$distributionUrl" -O "$TMP_DOWNLOAD_DIR/$distributionUrlName" || die "wget: Failed to fetch $distributionUrl"
# elif [ -z "${MVNW_USERNAME-}" ] && command -v curl >/dev/null; then
#   verbose "Found curl ... using curl"
#   curl ${__MVNW_QUIET_CURL:+"$__MVNW_QUIET_CURL"} -f -L -o "$TMP_DOWNLOAD_DIR/$distributionUrlName" "$distributionUrl" || die "curl: Failed to fetch $distributionUrl"
# elif set_java_home; then
#   verbose "Falling back to use Java to download"
#   javaSource="$TMP_DOWNLOAD_DIR/Downloader.java"
#   targetZip="$TMP_DOWNLOAD_DIR/$distributionUrlName"
#   cat >"$javaSource" <<-END
# 	public class Downloader extends java.net.Authenticator
# 	{
# 	  protected java.net.PasswordAuthentication getPasswordAuthentication()
# 	  {
# 	    return new java.net.PasswordAuthentication( System.getenv( "MVNW_USERNAME" ), System.getenv( "MVNW_PASSWORD" ).toCharArray() );
# 	  }
# 	  public static void main( String[] args ) throws Exception
# 	  {
# 	    setDefault( new Downloader() );
# 	    java.nio.file.Files.copy( java.net.URI.create( args[0] ).toURL().openStream(), java.nio.file.Paths.get( args[1] ).toAbsolutePath().normalize() );
# 	  }
# 	}
# 	END
#   # For Cygwin/MinGW, switch paths to Windows format before running javac and java
#   verbose " - Compiling Downloader.java ..."
#   "$(native_path "$JAVACCMD")" "$(native_path "$javaSource")" || die "Failed to compile Downloader.java"
#   verbose " - Running Downloader.java ..."
#   "$(native_path "$JAVACMD")" -cp "$(native_path "$TMP_DOWNLOAD_DIR")" Downloader "$distributionUrl" "$(native_path "$targetZip")"
# fi

# # If specified, validate the SHA-256 sum of the Maven distribution zip file
# if [ -n "${distributionSha256Sum-}" ]; then
#   distributionSha256Result=false
#   if [ "$MVN_CMD" = mvnd.sh ]; then
#     echo "Checksum validation is not supported for maven-mvnd." >&2
#     echo "Please disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
#     exit 1
#   elif command -v sha256sum >/dev/null; then
#     if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | sha256sum -c >/dev/null 2>&1; then
#       distributionSha256Result=true
#     fi
#   elif command -v shasum >/dev/null; then
#     if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | shasum -a 256 -c >/dev/null 2>&1; then
#       distributionSha256Result=true
#     fi
#   else
#     echo "Checksum validation was requested but neither 'sha256sum' or 'shasum' are available." >&2
#     echo "Please install either command, or disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
#     exit 1
#   fi
#   if [ $distributionSha256Result = false ]; then
#     echo "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised." >&2
#     echo "If you updated your Maven version, you need to update the specified distributionSha256Sum property." >&2
#     exit 1
#   fi
# fi

# # unzip and move
# if command -v unzip >/dev/null; then
#   unzip ${__MVNW_QUIET_UNZIP:+"$__MVNW_QUIET_UNZIP"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -d "$TMP_DOWNLOAD_DIR" || die "failed to unzip"
# else
#   tar xzf${__MVNW_QUIET_TAR:+"$__MVNW_QUIET_TAR"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -C "$TMP_DOWNLOAD_DIR" || die "failed to untar"
# fi
# printf %s\\n "$distributionUrl" >"$TMP_DOWNLOAD_DIR/$distributionUrlNameMain/mvnw.url"
# mv -- "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" "$MAVEN_HOME" || [ -d "$MAVEN_HOME" ] || die "fail to move MAVEN_HOME"

# clean || :
# exec_maven "$@"
