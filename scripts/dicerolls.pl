#!/usr/bin/perl

use strict;

## run monte carlo sims on these dice, find out how many regions
my @rolls = ();
#push @rolls, [2,2,1,1,1,1];
#push @rolls, [2,2,1,1,1];
#push @rolls, [2,3,3,1,1];
#push @rolls, [2,3,1,1,1];
#push @rolls, [2,3,3,1,1,1];
push @rolls, [4];
push @rolls, [2];

my $required = 3;

my $runs = 100000;

foreach my $proll (@rolls) {
   my @roll = @{$proll};
   my $size = scalar(@roll);
   my @hit_counter = ();

   print "Roll (";
   foreach my $dice (@roll) {
      print $dice . ",";
   }
   print "):\t";
   
   foreach my $i (0..$runs-1) {
      my $regions_hit = 0;
      foreach my $region (0..$size-1) {
         my $rrolls = $roll[$region];
#3         print "testing success: $rrolls -> " . rand() . " " . (1-.5**$rrolls) . "\n";
         my $success = rand() < 1-.5**$rrolls;
         if($success) {
            $regions_hit = $regions_hit +1;
#            print "success!";
         }
      }
#      print "$regions_hit regions hit!\n";
      $hit_counter[$regions_hit] = $hit_counter[$regions_hit]+1.0;
   }

   my $total_prob = 0;
   my $prob_so_far = 0;
   foreach my $count (0..@hit_counter-1) {
      my $prob = $hit_counter[$count] / ($runs*1.0);
      #print " P($count)=$prob";
      print "P(>$count)=" . (1-$prob_so_far) . "\t";
      $prob_so_far += $prob;
      if($count >= $required) {
          $total_prob += $prob;
      }
   }
#   print "    -- P(hits>=$required) = $total_prob\n";
   print "\n";
}
