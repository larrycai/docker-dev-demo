docker run --rm -it --name jenkins --link dashing:dashing -v $PWD:/opt/jenkins/data/jobs/longjobs/workspace -p 8080:8080 larrycai/jenkins
