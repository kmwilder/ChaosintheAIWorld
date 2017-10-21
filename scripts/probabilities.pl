#!/usr/bin/perl

use strict;

my $min_dice = (@ARGV > 0) ? $ARGV[0] : 1;
my $num_dice = (@ARGV > 0) ? $ARGV[0] : 8;
my $prob_limit = (@ARGV > 1) ? $ARGV[1] : .001;
my $runs = 1000000;

foreach my $rolls ($min_dice..$num_dice) {
   my @hit_counter = ();

   foreach my $run (0..$runs-1) {
      my $hits = 0;
      foreach my $roll (1..$rolls) {
         my $result = 6;
         while($result == 6) {
            $result = int(rand(6))+1;
            #print "Rolled a $result\n";
            $hits = $hits + 1 if ($result >= 4);
         }
      }
#      print "Saw $hits hits on roll $run\n";
      $hit_counter[$hits] = $hit_counter[$hits] + 1;
   }

   my $prob = 1.0;
   print "Dice\tHits\tProb\tProb>=\n";

   foreach my $hits (0..scalar(@hit_counter)-1) {
      my $this_prob = $hit_counter[$hits] / $runs;
      next if $this_prob <= $prob_limit;
      print $rolls . "\t" . $hits . "\t" . sprintf("%.5f", $this_prob) . "\t" . sprintf("%.5f", $prob) . "\t \n";
      $prob -= $this_prob;
   }
}
