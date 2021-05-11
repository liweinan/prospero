/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.prospero.descriptors;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.redhat.prospero.xml.ManifestReader;
import com.redhat.prospero.xml.XmlException;

public class Manifest {

   private final List<Artifact> artifacts;
   private final Path manifestFile;
   private final List<Package> packages;

   public Manifest(List<Artifact> artifacts, List<Package> packages, Path manifestFile) {
      this.artifacts = artifacts;
      this.packages = packages;
      this.manifestFile = manifestFile;
   }

   public static Manifest parseManifest(Path manifestPath) throws XmlException {
      return ManifestReader.parse(manifestPath.toFile());
   }

   public List<Artifact> getArtifacts() {
      return new ArrayList<>(artifacts);
   }

   public List<Package> getPackages() {
      return packages;
   }

   public Path getManifestFile() {
      return manifestFile;
   }

   public void updateVersion(Artifact newVersion) {
      // we can only update if we have old version of the same artifact
      Artifact oldArtifact = null;
      for (Artifact artifact : artifacts) {
         if (artifact.groupId.equals(newVersion.groupId) && artifact.artifactId.equals(newVersion.artifactId) && artifact.classifier.equals(newVersion.classifier)) {
            oldArtifact = artifact;
            break;
         }
      }

      if (oldArtifact == null) {
         throw new RuntimeException("Previous verison of " + newVersion.getFileName() + " not found.");
      }

      artifacts.remove(oldArtifact);
      artifacts.add(newVersion);
   }

   public Artifact find(String group, String name, String classifier) {
      for (Artifact artifact : artifacts) {
         if (artifact.groupId.equals(group) && artifact.artifactId.equals(name) && artifact.classifier.equals(classifier)) {
            return artifact;
         }
      }
      return null;
   }

   public static class Artifact {

      public final String groupId;
      public final String artifactId;
      public final String version;
      public final String classifier;

      public Artifact(String groupId, String artifactId, String version, String classifier) {
         this.groupId = groupId;
         this.artifactId = artifactId;
         this.version = version;
         this.classifier = classifier;
      }

      public String getFileName() {
         if (classifier == null || classifier.length() == 0) {
            return String.format("%s-%s.jar", artifactId, version);
         } else {
            return String.format("%s-%s-%s.jar", artifactId, version, classifier);
         }
      }

      public Path getRelativePath() {
         List<String> path = new ArrayList<>();
         String start = null;
         for (String f : groupId.split("\\.")) {
            if (start == null) {
               start = f;
            } else {
               path.add(f);
            }
         }
         path.add(artifactId);
         path.add(version);
         path.add(getFileName());

         return Paths.get(start, path.toArray(new String[]{}));
      }

      public Artifact newVersion(String newVersion) {
         return new Artifact(groupId, artifactId, newVersion, classifier);
      }
   }

   public static class Package {

      public final String groupId;
      public final String artifact;
      public final String version;

      public Package(String groupId, String artifact, String version) {
         this.groupId = groupId;
         this.artifact = artifact;
         this.version = version;
      }

      public String getFileName() {
         return String.format("%s-%s.zip", artifact, version);
      }

      public Path getRelativePath() {
         List<String> path = new ArrayList<>();
         String start = null;
         for (String f : groupId.split("\\.")) {
            if (start == null) {
               start = f;
            } else {
               path.add(f);
            }
         }
         path.add(artifact);
         path.add(version);
         path.add(getFileName());

         return Paths.get(start, path.toArray(new String[]{}));
      }
   }
}
